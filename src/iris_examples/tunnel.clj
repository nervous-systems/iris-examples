(ns iris-examples.tunnel
  (:require [clojure.data.generators :as gen]
            [clojure.core.async :as async :refer [<!! >!! <! >!]]
            [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection ServiceHandler Service Tunnel]
           [com.karalabe.iris.exceptions RemoteException ClosedException]
           [java.io IOException]))

(defn receive!! [^Tunnel tunnel]
  (try
    (common/unpack-message (.receive tunnel))
    (catch ClosedException _
      nil)))

(defn send!! [^Tunnel tunnel value]
  (.send tunnel (common/pack-message value)))

(defn close! [^Tunnel tunnel]
  (.close tunnel))

(defn tunnel-wrapper [read-or-write tunnel & [{:keys [chan]}]]
  (let [chan (or chan (async/chan))]
    (async/thread
      (when (or (= read-or-write :write)
                (some->> tunnel receive!! (>!! chan)))
        (loop []
          (when-let [out-value (<!! chan)]
            (send!! tunnel out-value)
            (when-let [in-value (receive!! tunnel)]
              (when (>!! chan in-value)
                (recur)))))
        (close! tunnel)
        (async/close! chan)))
    chan))

(defn tunnel!!
  [{:keys [service connection timeout] :or {timeout 1000}} & [opts]]
  (let [tunnel (.tunnel connection service timeout)]
    (tunnel-wrapper :write tunnel opts)))

(defn tunnel! [conn]
  (let [chan (async/chan)]
    (async/thread-call #(tunnel!! conn {:chan chan}))
    chan))

(defn create-handler [tunnel-callback]
  (reify ServiceHandler
    (handleTunnel [_ tunnel]
      (tunnel-callback (tunnel-wrapper :read tunnel)))))

(defn echo-client! [chan]
  (async/go-loop [echo true]
    (let [op (if echo
               [:echo (gen/anything)]
               [:last])]
      (log/info ">" op)
      (>! chan op))
    (when-let [response (<! chan)]
      (log/info "<" response)
      (<! (async/timeout 500))
      (recur (not echo)))))


(defn echo-server! [chan tunnel!]
  (async/go-loop [last-value nil]
    (when-let [[command value :as op] (<! chan)]
      (case command
        :last (>! chan [:last last-value])
        :echo (if (zero? (rand-int 2))
                (let [proxy-chan (tunnel!)]
                  (>! proxy-chan [:echo op])
                  (async/pipe proxy-chan chan))
                (>! chan op)))
      (recur value))))

(defn -main [& args]
  (let [port (common/cli-args->port args)
        conn {:connection (Connection. port) :service "echo-service"}]
    (Service. port "echo-service"
              (create-handler #(echo-server! % (partial tunnel! conn))))
    (echo-client! (tunnel!! conn))))
