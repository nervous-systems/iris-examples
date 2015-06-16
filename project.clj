(defproject io.nervous/iris-examples "0.1.0-SNAPSHOT"
  :aot [iris-examples.req-resp.service
        iris-examples.req-resp.client
        iris-examples.tunnel
        iris-examples.broadcast.receive
        iris-examples.broadcast.send
        iris-examples.pub-sub.pub
        iris-examples.pub-sub.sub]
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/tools.cli "0.3.1"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.clojure/data.generators "0.1.2"]
                 [com.cognitect/transit-clj "0.8.275"]
                 [com.karalabe.iris/iris "1.0.0"]
                 [ch.qos.logback/logback-classic "1.1.2"]])
