(ns iris-example-req-resp.common
  (:require [cognitect.transit :as transit])
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream]))

(defn pack-message [m]
  (let [o (ByteArrayOutputStream.)]
    (transit/write (transit/writer o :msgpack) m)
    (let [result (.toByteArray o)]
      (.reset o)
      result)))

(defn unpack-message [byte-array]
  (let [i (ByteArrayInputStream. byte-array)]
    (transit/read (transit/reader i :msgpack))))
