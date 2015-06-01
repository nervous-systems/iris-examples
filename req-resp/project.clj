(defproject io.nervous/iris-example-req-resp "0.1.0-SNAPSHOT"
  :aot [iris-example-req-resp.service
        iris-example-req-resp.client]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.cognitect/transit-clj "0.8.275"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [com.karalabe.iris/iris "1.0.0"]
                 [org.clojure/tools.logging "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.2"]])
