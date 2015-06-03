(ns iris-examples.req-resp.service
  (:require [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection ServiceHandler Service]
           [com.karalabe.iris.exceptions RemoteException]))

(defmulti  bit-service :command)
(defmethod bit-service :default [_]
  (throw (Exception. "Unknown command!")))

(defmethod bit-service :random [_]
  (rand-int 2))

(defmethod bit-service :shift [{:keys [number places direction]}]
  (case direction
    :left  (bit-shift-left  number places)
    :right (bit-shift-right number places)))

(defn log-message [m]
  (log/info "Received bit-service message:" m)
  m)

(defn create-handler []
  (reify ServiceHandler
    (handleRequest [_ byte-array]
      (try
        (-> byte-array
            common/unpack-message
            log-message
            bit-service
            common/pack-message)
        (catch Exception e
          (throw (RemoteException. (.getMessage e) e)))))))

(defn -main [& args]
  (Service. (common/cli-args->port args)
            common/bit-service
            (create-handler)))
