(ns iris-examples.broadcast.send
  (:require [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection]))

(defn broadcast [{:keys [port reps]}]
  (let [conn (Connection. port)]
    (dotimes [i reps]
      (let [msg (-> i common/generate-event common/pack-message)]
        (.broadcast conn common/bit-service msg)))
    (.close conn)))

(defn -main [& args]
  (broadcast {:port (common/cli-args->port args)
              :reps (common/cli-args->int args Long/MAX_VALUE)}))
