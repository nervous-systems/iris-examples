(ns iris-examples.broadcast.send
  (:require [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection]))

(defn -main [& args]
  (let [conn (Connection. (common/cli-args->port args))]
    (dotimes [i (common/cli-args->int args Long/MAX_VALUE)]
      (let [msg (-> i common/generate-event common/pack-message)]
        (.broadcast conn common/bit-service msg)))
    (.close conn)))
