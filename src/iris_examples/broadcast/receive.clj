(ns iris-examples.broadcast.receive
  (:require [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Service ServiceHandler]))

(defn create-broadcast-handler []
  (reify ServiceHandler
    (handleBroadcast [_ byte-array]
      (log/info "Received broadcast:" (common/unpack-message byte-array)))))

(defn -main [& args]
  (Service. (common/cli-args->port args)
            common/bit-service
            (create-broadcast-handler)))
