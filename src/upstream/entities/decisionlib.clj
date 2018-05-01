(ns upstream.entities.decisionlib
  (:require [upstream.config :as config])
  (:gen-class))

(defmacro defpredicate
  "Macro for defining decision functions"
  [name operation]
  (list 'def (str "_" name "_")
        (list 'fn '[entity-context]
                  (list operation 'entity-context))))

(defmacro defaction
  "Macro for defining decision functions"
  [name operation]
  (list 'def (str "_" name "_")
      (list 'fn '[res-map entity-context]
              (list operation 'res-map 'entity-context))))
