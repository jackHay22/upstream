(ns upstream.utilities.log
  (:require [clj-http.client :as httpclient]
            [upstream.config :as config])
  (:gen-class))

(defn write-log
  "write log message to std out"
  [msg & args]
  (if @config/HEADLESS-SERVER?
    (println "Upstream =>" msg (reduce str args))))
