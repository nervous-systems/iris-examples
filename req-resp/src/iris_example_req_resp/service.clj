(ns iris-example-req-resp.service
  (:require [iris-example-req-resp.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection ServiceHandler Service]
           [com.karalabe.iris.exceptions RemoteException]))

(defmulti  bit-command :command)
(defmethod bit-command :default [_]
  (throw (Exception. "Unknown command!")))

(defmethod bit-command :random [_]
  (rand-int 2))

(defmethod bit-command :shift [{:keys [number places direction]}]
  (case direction
    :left  (bit-shift-left  number places)
    :right (bit-shift-right number places)))

(defn log-message [m]
  (log/info "Received bit-service message:" (pr-str m))
  m)

(defn create-handler [drop-latch]
  (reify ServiceHandler

    (handleRequest [_ byte-array]
      (try
        (-> byte-array
            common/unpack-message
            log-message
            bit-command
            common/pack-message)
        (catch Exception e
          (throw (RemoteException. (.getMessage e) e)))))

    (handleDrop [_ reason]
      (deliver drop-latch true))))

(defn -main []
  (let [drop-latch (promise)
        service (Service. 55555 "bit-service" (create-handler drop-latch))]
    @drop-latch))
