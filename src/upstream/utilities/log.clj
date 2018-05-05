(ns upstream.utilities.log
  (:require [clj-http.client :as httpclient]
            [upstream.config :as config])
  (:gen-class))

(import java.io.File)
(import javax.swing.JFrame)
(import javax.swing.JLabel)

(def debug-window (atom nil))
(def internal-debugger? (atom false))

(defn debugger-add-message
  "add a new message"
  [message]
  (.add @debug-window (JLabel. message)))

(defn write-log
  "write log message to std out"
  [msg & args]
  (cond @config/HEADLESS-SERVER?
      (println "Upstream =>" msg (reduce str args))
        @internal-debugger? (debugger-add-message (str "Upstream =>" msg (reduce str args)))))

(defn save-critical-log
  "save a critical error message to a debug file"
  [debug-filename message]
  (let [log-dir (File. (str (System/getProperty "user.home") File/separator ".upstream"))
        log-file (File. (str (System/getProperty "user.home") File/separator ".upstream" File/separator debug-filename))]
        (.createNewFile log-file) ;will only create if doesn't exist
        (if (not (.exists log-dir)) (.mkdir log-dir))
        (with-open [log-writer (clojure.java.io/writer log-file)]
            (.write log-writer (pr-str message)))))

(defn start-log-window
  "start an ever-present logging window for
  debug when running app without command line"
  [title]
  (let [new-frame (JFrame. title)]
    (reset! internal-debugger? true)
    (reset! debug-window
    (doto new-frame
      (.setAlwaysOnTop true)
      (.setAlwaysOnTop true)
      (.setLocationByPlatform true)
      (.pack)
      (.setVisible true)))))
