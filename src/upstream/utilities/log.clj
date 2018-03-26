(ns upstream.utilities.log
  (:gen-class))

(defn write-log
  "write log message to std out"
  [msg & args]
  (println "Upstream =>" msg (reduce str args)))
