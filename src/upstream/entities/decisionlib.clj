(ns upstream.entities.decisionlib
  (:require [upstream.config :as config]
            [upstream.utilities.spacial :as spacialutil])
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

(defoperator enemy-visible?
  (fn [entity-context]
    (let [chunk-positions (:all-positions entity-context)]


      ;returns transformed entity-context
    )))

(defoperator attack-closest
  (fn [entity-context]
    (let [chunk-positions (:all-positions entity-context)]


    ;returns transformed entity-context
  )))

(defn resolve-loaded-name
  "resolve action to qualified function name"
  [function-name]
  (ns-resolve *ns*
    (symbol (str "upstream.entities.decisionlib/" function-name "_"))))

(defn evaluate-predicates
  "take list of predicates and evaluate"
  [predicates-list entity-context]
  (reduce #((first predicates-list) %1 %2)
      (map #(% entity-context) (reset predicates-list))))

(defn evaluate-actions
  "take list of actions and operate on state"
  [actions-list entity-context]
  (reduce #(%2 %1) entity-context actions-list))
