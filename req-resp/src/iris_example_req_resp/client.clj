(ns iris-example-req-resp.client
  (:require [iris-example-req-resp.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection]
           [com.karalabe.iris.exceptions RemoteException]))

(defn random-request []
  (let [cmd (rand-nth [:random :shift :super-invalid])]
    (cond-> {:command cmd}
      (= cmd :shift) (conj {:direction (rand-nth [:left :right])
                            :number (rand-int 10)
                            :places (rand-int 10)}))))

(defn log-response [req resp]
  (log/info "Received response:" req "=>" (common/unpack-message resp)))

(defn log-response-error [req e]
  (log/warn "Oops, received error:" req "=>" (.getMessage e)))

(defn make-noisy-request! [conn req]
  (try
    (let [resp (.request conn "bit-service" (common/pack-message req) 1000)]
      (log-response req resp)
      true)
    (catch RemoteException e
      (log-response-error req e))))

(defn -main [& args]
  (let [conn (Connection. (common/cli-args->port args))]
    (loop [req (random-request)]
      (if (make-noisy-request! conn req)
        (recur (random-request))
        (.close conn)))))
