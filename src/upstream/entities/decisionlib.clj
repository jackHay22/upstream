(ns upstream.entities.decisionlib
  (:require [upstream.config :as config]
            [upstream.utilities.spacial :as spacialutil])
  (:gen-class))

(defmacro defoperator
  "Macro for defining decision functions (predefined)"
  [name operation]
  (list 'def (symbol (str name "_"))
        (list 'fn '[entity-context]
              (list operation 'entity-context))))

(defmacro redefine
  "Macro for redefining boolean operators"
  [name operator]
  (list 'def (symbol (str name "_"))
        (list 'fn '[operator-list]
              (list 'reduce operator 'operator-list))))

(redefine || 'or)
(redefine && 'and)

(defoperator enemy-visible?
  (fn [entity-context]
    (let [chunk-positions (:all-positions entity-context)]


      ;returns boolean
    )))

(defoperator attack-closest
  (fn [entity-context]
    (let [chunk-positions (:all-positions entity-context)]


    ;returns transformed entity-context
  )))

(defn resolve-loaded-name
  "resolve action to qualified function name"
  [function-name family-marker]
  (ns-resolve *ns*
    (symbol (str "upstream.entities.decisionlib/" function-name family-marker))))

(defn load-decision-subcomponent
  [input]
  (resolve-loaded-name input "_"))

(defn evaluate-predicates
  "take list of predicates and evaluate"
  [predicates-list entity-context]
  ((first predicates-list) (map #(% entity-context) (rest predicates-list))))

(defn evaluate-actions
  "take list of actions and operate on state"
  [actions-list entity-context]
  (reduce #(%2 %1) entity-context actions-list))
