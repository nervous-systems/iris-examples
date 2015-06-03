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

(defn generate-event [i]
  {:event :integer
   :data {:value i}
   :time (System/currentTimeMillis)})

(defn -main [& args]
  (let [chan  (async/chan)
        limit (common/cli-args->int args Long/MAX_VALUE)
        conn  (Connection. (common/cli-args->port args))]
    (publish-loop conn chan)
    (dotimes [i limit]
      (async/>!! chan (generate-event i)))
    (.close conn)))
