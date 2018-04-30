(ns upstream.entities.decisionlib
  (:require [upstream.config :as config])
  (:gen-class))

(def prefix-mappings
  {})

;Note: all predicates take their entity context
;All actions take the current result map and their entity context

(def decision-function-mappings
  {:action {:prefix-1 {}}
   :predicate {:prefix-1 {:and :and}}})

(defn get-decision-function
  "take predicate or action and return function"
  [statement-type identifier]
  (get
    (get
      (statement-type decision-function-mappings)
      (first identifier))
    (second identifier)))

(def get-decision-function-cached (memoize get-decision-function))

(defn add-lookup-prefix
  "for each action, add a lookup prefix for more efficient search"
  [symbol]
  (list (symbol prefix-mappings) symbol))
