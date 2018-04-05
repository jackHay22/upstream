(ns upstream.utilities.save
  (:require [upstream.config :as config]
            [upstream.entities.entitymanager :as entity-manager]
            [clojure.java.io :as io])
  (:gen-class))

(defn prepare-for-storage
  "take entity state and extract fields to store"
  [e-state]
  {:logical-entity-id (:logical-entity-id e-state)
   :decisions (:decisions e-state)
   :position-x (:position-x e-state)
   :position-y (:position-y e-state)
   :facing (:facing e-state)
   :current-action (:current-action e-state)})

(defn save-state
  "save entity states to file"
  [entity-states]
  (let [save-transform (map prepare-for-storage entity-states)]
  (with-open [save-writer (clojure.java.io/writer (io/resource config/SAVE-PATH))]
      (.write save-writer (pr-str save-transform)))))

(defn load-from-save
  "load entities state from save file, take list of config states to merge with"
  [to-merge]
  (with-open [save-reader (clojure.java.io/reader (io/resource config/SAVE-PATH))]
    (let [raw-save-state (clojure.string/join "\n" (line-seq save-reader))
          to-load (if (empty? raw-save-state) (repeat nil) (read-string raw-save-state))]
          (entity-manager/load-entities (map merge to-merge to-load)))))
