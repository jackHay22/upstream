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

(defpredicate and_ and)
(defpredicate or_ or)
(defpredicate enemy-visible?_ #(println %1))
(defaction attack-closest_ #(println %1))

(defn resolve-loaded-name
  "resolve action to qualified function name"
  [function-name]
  (ns-resolve *ns*
    (symbol (str "upstream.entities.decisionlib/" function-name "_"))))

(defn evaluate-predicates
  "take list of predicates and evaluate"
  [predicates-list entity-context]
  (reduce #((first predicates-list) %1 (%2 entity-context)) true predicates-list))

(defn evaluate-actions
  "take list of actions and operate on state"
  [actions-list entity-context] ;TODO: add res-map
  (reduce #(%2 %1) entity-context actions-list))
