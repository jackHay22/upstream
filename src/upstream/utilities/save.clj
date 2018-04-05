(ns upstream.utilities.save
  (:require [upstream.config :as config]
            [upstream.entities.entitymanager :as entity-manager]
            [clojure.java.io :as io])
  (:gen-class))

(import java.io.File)

(defn prepare-for-storage
  "take entity state and extract fields to store"
  [e-state]
  {:logical-entity-id (:logical-entity-id e-state)
   :decisions (:decisions e-state)
   :position-x (:position-x e-state)
   :position-y (:position-y e-state)
   :facing (:facing e-state)
   :current-action (:current-action e-state)})

(defn get-user-save-location
  "create save location, create directory if doesn't exist"
  [save-filename]
  (let [save-dir (File. (str (System/getProperty "user.home") File/separator ".upstream"))]
    (if (not (.exists save-dir)) (.mkdir save-dir))
    (str save-dir File/separator save-filename)))

(defn save-state
  "save entity states to file"
  [entity-states]
  (let [save-transform (map prepare-for-storage entity-states)]
  (with-open [save-writer (clojure.java.io/writer (get-user-save-location config/SAVE-FILE))]
      (.write save-writer (pr-str save-transform)))))

(defn load-from-save
  "load entities state from save file, take list of config states to merge with"
  [to-merge]
  (with-open [save-reader (clojure.java.io/reader (get-user-save-location config/SAVE-FILE))]
    (let [raw-save-state (clojure.string/join "\n" (line-seq save-reader))
          to-load (if (empty? raw-save-state) (repeat nil) (read-string raw-save-state))]
          (entity-manager/load-entities (map merge to-merge to-load)))))
