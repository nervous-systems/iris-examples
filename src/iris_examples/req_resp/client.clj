(ns iris-examples.req-resp.client
  (:require [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection]))

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
    (let [resp (.request
                conn
                common/bit-service
                (common/pack-message req) 1000)]
      (log-response req resp)
      true)
    (catch Exception e
      (log-response-error req e)
      nil)))

(defn -main [& args]
  (let [conn (Connection. (common/cli-args->port args))]
    (loop []
      (if (make-noisy-request! conn (random-request))
        (recur)
        (.close conn)))))
