(ns iris-examples.pub-sub.pub
  (:require [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection]))

(defn publish [& [{:keys [port reps] :or {port 55555 reps 1}}]]
  (let [conn (Connection. port)]
    (dotimes [i reps]
      (->> i
           common/generate-event
           common/pack-message
           (.publish conn common/topic)))
    (.close conn)))

(defn -main [& args]
  (publish {:port (common/cli-args->port args)
            :reps (common/cli-args->int args Long/MAX_VALUE)}))
