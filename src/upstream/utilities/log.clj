(ns upstream.utilities.log
  (:require [clj-http.client :as httpclient]
            [upstream.config :as config])
  (:gen-class))

(import java.io.File)

(defn write-log
  "write log message to std out"
  [msg & args]
  (if @config/HEADLESS-SERVER?
    (println "Upstream =>" msg (reduce str args))))

(defn save-critical-log
  "save a critical error message to a debug file"
  [debug-filename message]
  (let [log-dir (File. (str (System/getProperty "user.home") File/separator ".upstream"))
        log-file (File. (str (System/getProperty "user.home") File/separator ".upstream" File/separator debug-filename))]
        (.createNewFile log-file) ;will only create if doesn't exist
        (if (not (.exists log-dir)) (.mkdir log-dir))
        (with-open [log-writer (clojure.java.io/writer log-file)]
            (.write log-writer (pr-str message)))))
