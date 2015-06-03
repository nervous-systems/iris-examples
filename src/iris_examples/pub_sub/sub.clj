(ns iris-examples.pub-sub.sub
  (:require [clojure.core.async :as async]
            [iris-examples.common :as common]
            [clojure.tools.logging :as log])
  (:import [com.karalabe.iris Connection TopicHandler TopicLimits]))

(defn create-topic-handler [chan]
  (reify TopicHandler
    (handleEvent [_ byte-array]
      (async/>!! chan (common/unpack-message byte-array)))))

(defn topic-limits [{:keys [threads bytes]}]
  (let [limits (TopicLimits.)]
    (when threads
      (set! (. limits eventThreads) threads))
    (when bytes
      (set! (. limits eventMemory)  bytes))
    limits))

(defn consume-loop [chan]
  (async/go-loop []
    (log/info "Consumed:" (async/<! chan))
    (recur)))

(defn -main [& args]
  (let [conn (Connection. (common/cli-args->port args))
        chan (async/chan)]
    (consume-loop chan)
    (.subscribe conn
                "iris-examples/pub-sub/events"
                (create-topic-handler chan))))
