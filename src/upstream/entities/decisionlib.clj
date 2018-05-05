(ns upstream.entities.decisionlib
  (:require [upstream.config :as config])
  (:gen-class))

(defmacro defoperator
  "Macro for defining decision functions"
  [name operation]
  (list 'def (symbol (str name "_"))
        (list 'fn '[entity-context]
              (list operation 'entity-context))))

(defmacro redefine
  "Macro for redefining boolean operators"
  [name operator]
  (list 'def (symbol (str name "_"))
        (list 'fn '[& operators]
              (list 'reduce operator 'operators))))

(redefine || 'or)
(redefine && 'and)

(defoperator enemy-visible? #(println %1))
(defoperator attack-closest #(println %1))
;:all-positions (access)
;actions operate on :control-input

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
  [actions-list entity-context]
  (reduce #(%2 %1) entity-context actions-list))
