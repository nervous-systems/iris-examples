(ns iris-examples.pub-sub.pub
  (:require [clojure.core.async :as async]
            [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection]))

(defn publish-loop [conn chan]
  (async/thread
    (loop []
      (let [msg (-> chan async/<!! common/pack-message)]
        (.publish
         conn
         "iris-examples/pub-sub/events"
         msg)
        (recur)))))

(defn generate-event []
  {:event :random-bit
   :data {:value (rand-int 2)}
   :time (System/currentTimeMillis)})

(defn -main [& args]
  (let [conn (Connection. (common/cli-args->port args))
        chan (async/chan)]
    (publish-loop conn chan)
    (loop []
      (async/>!! chan (generate-event))
      (recur))))
