(ns upstream.utilities.log
  (:require [clj-http.client :as httpclient]
            [upstream.config :as config])
  (:gen-class))

(defn write-log
  "write log message to std out"
  [msg & args]
  (println "Upstream =>" msg (reduce str args)))

(defn write-log-sumologic
  "send a log to sumologic endpt"
  [message]
  (httpclient/get (str config/SUMOLOGIC-ENDPOINT "?version=" config/SERVER-VERSION ", message=" message) {:async? true}
      (fn [response] (println "Upstream => Sumologic log response status:" (:status response)))
      (fn [exception] (println "Upstream => Sumologic log exception: " (.getMessage exception)))))

(defn write-log-all
  "write log to stdout and sumologic"
  [msg & args]
  (let [log-message (str msg (reduce str args))]
    (do
      (println "Upstream =>" log-message)
      (write-log-sumologic (clojure.string/trim log-message)))))
