(defproject upstream "0.1.5-SNAPSHOT"
  :description "Isometric Game by Jack Hay"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [clj-http "3.7.0"]
                 [org.clojure/core.async "0.3.443"]]
  :main ^:skip-aot upstream.core
  :jvm-opts ["-Xdock:name=Upstream"]
  ;:uberjar-name "upstream-standalone.jar"
  ;:jvm-opts ["-Xmx1g" "-server"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
