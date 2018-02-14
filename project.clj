(defproject upstream "0.1.0-SNAPSHOT"
  :description "..."
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot upstream.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
