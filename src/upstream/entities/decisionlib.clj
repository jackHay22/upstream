(ns upstream.entities.decisionlib
  (:require [upstream.config :as config])
  (:gen-class))

(defmacro defpredicate
  "Macro for defining decision functions"
  [name operation]
  (list 'def name
        (list 'fn '[entity-context]
                  (list operation 'entity-context))))

(defmacro defaction
  "Macro for defining decision functions"
  [name operation]
  (list 'def name
      (list 'fn '[res-map entity-context]
              (list operation 'res-map 'entity-context))))

;EXAMPLE
(defaction test_ #(println %1))

(defn resolve-loaded-name
  "resolve action to qualified function name"
  [function-name]
  (ns-resolve *ns*
    (symbol (str "upstream.entities.decisionlib/" function-name "_"))))

(defn evaluate-predicates
  "take list of predicates and evaluate"
  [predicates-list]

  )

(defn evaluate-actions
  "take list of actions and operate on state"
  [actions-list]

  )
