(ns upstream.entities.decisionlib
  (:require [upstream.config :as config])
  (:gen-class))

(def prefix-mappings
  {})

(def decision-function-mappings
  {:action {:prefix-1 {}}
   :predicate {:prefix-1 {}}})

(defn get-decision-function
  "take predicate or action and return function"
  [statement-type identifier]
  (if (or (= true (first identifier)) (= false (first identifier))) (first identifier)
  (get
    (get
      (statement-type decision-function-mappings)
      (first identifier))
    (second identifier))))

(defn add-lookup-prefix
  "for each action, add a lookup prefix for more efficient search"
  [symbol]
)
