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

(defn make-noisy-request! [conn req]
  (try
    (let [resp (.request conn "bit-service" (common/pack-message req) 1000)]
      (log/info "Received response:" req "=>" (common/unpack-message resp))
      true)
    (catch RemoteException e
      (log/warn "Oops, received error:" req "=>" (.getMessage e)))))

(defn -main []
  (let [conn (Connection. 55555)]
    (doseq [req (repeatedly random-request)]
      (when-not (make-noisy-request! conn req)
        (System/exit 1)))))
