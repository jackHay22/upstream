(ns upstream.utilities.save
  (:require [upstream.config :as config]
            [clojure.java.io :as io]
            [upstream.utilities.log :as log])
  (:gen-class))

(import java.io.File)

(def MUTEX_LOCK (Object.))

(defn prepare-for-storage
  "take entity state and extract fields to store"
  [e-state]
  {:logical-entity-id (:logical-entity-id e-state)
   :position-x (:position-x e-state)
   :position-y (:position-y e-state)
   :facing (:facing e-state)
   :current-action (:current-action e-state)})

(defn build-path
  "build path from sub directories"
  [& subdirs])

(defn get-user-save-location
  "create save location, create directory if doesn't exist"
  [save-filename]
  (let [save-dir (File. (str (System/getProperty "user.home") File/separator ".upstream"))
        save-file (File. (str (System/getProperty "user.home") File/separator ".upstream" File/separator save-filename))]
      (locking MUTEX_LOCK
        (.createNewFile save-file) ;will only create if doesn't exist
        (if (not (.exists save-dir)) (.mkdir save-dir))
        save-file)))

(defn save-state
  "save entity states to file"
  [entity-states]
  (let [save-transform (map prepare-for-storage entity-states)]
  (locking MUTEX_LOCK
    (with-open [save-writer (clojure.java.io/writer (get-user-save-location config/SAVE-FILE))]
      (.write save-writer (pr-str save-transform))))))

(defn load-from-save
  "load entities state from save file, take list of config states to merge with"
  [to-merge]
  (do
    (with-open [save-reader (clojure.java.io/reader (get-user-save-location config/SAVE-FILE))]
      (let [raw-save-state (clojure.string/join "\n" (line-seq save-reader))
            to-load (if (empty? raw-save-state)
                        (repeat nil)
                      (try
                        (read-string raw-save-state)
                      (catch Exception e
                        (do
                          (log/write-log "Error loading saved game state, reverting to preset: \n\n" (pr-str to-merge))
                          (repeat nil)))))]
           (map merge to-merge to-load)))))
           ;TODO
    ;(reset! FILE-AVAILABLE? true))))

(defn start-autosaver
  "start autosaver"
  [state-reference]
  (.start (Thread.
      (do
        (Thread/sleep config/AUTO-SAVE-SLEEP)
        (loop []
          (Thread/sleep config/AUTO-SAVE-SLEEP)
          (locking MUTEX_LOCK (save-state @state-reference))
      (recur))))))

(defn overwrite-save!
  "overwrite game save with config/LEVEL-ONE-ENTITIES --
   restarting game and or fixing corruption"
  [to-reset] (save-state to-reset))
