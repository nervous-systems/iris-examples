(defproject io.nervous/iris-examples "0.1.0-SNAPSHOT"
  :aot [iris-examples.req-resp.service
        iris-examples.req-resp.client]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]

                 [com.cognitect/transit-clj "0.8.275"]
                 [com.karalabe.iris/iris "1.0.0"]
                 [ch.qos.logback/logback-classic "1.1.2"]])
