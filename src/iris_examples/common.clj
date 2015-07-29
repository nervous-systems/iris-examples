(ns iris-examples.common
  (:require [cognitect.transit :as transit]
            [clojure.tools.cli :as cli])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]))

(def topic "iris-examples/pub-sub/events")
(def bit-service "bit-service")

(defn pack-message [m]
  (let [o (ByteArrayOutputStream.)]
    (transit/write (transit/writer o :msgpack) m)
    (doto (.toByteArray o)
      (.reset))))

(defn unpack-message [byte-array]
  (let [i (ByteArrayInputStream. byte-array)]
    (transit/read (transit/reader i :msgpack))))

(def port-cli-options
  [["-p" "--port N" "Port number"
    :default 55555
    :parse-fn #(Integer/parseInt %)]])

(defn cli-args->int [cli-args default]
  (-> cli-args
      (cli/parse-opts
       [["-n" "--number N" nil
         :default default
         :parse-fn #(Integer/parseInt %)]])
      :options
      :number))

(defn cli-args->port [cli-args]
  (-> cli-args (cli/parse-opts port-cli-options) :options :port))

(defn generate-event [i]
  {:event :integer
   :data {:value i}
   :time (System/currentTimeMillis)})
