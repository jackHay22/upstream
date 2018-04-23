(ns upstream.entities.decisionlib
  (:require [upstream.config :as config])
  (:gen-class))

(def decision-function-mappings
  {:action {:prefix-1 {}}
   :predicate {:prefix-1 {}}})

(defn get-decision-mapping
  "take predicate or action and return function"
  [statement-type identifier]
  (get
    (get
      (statement-type decision-function-mappings)
      (first identifier))
    (second identifier)))

(defn add-lookup-prefix
  "for each action, add a lookup prefix for more efficient search"
  []
  (let [flattened-mappings (merge (:action decision-function-mappings)
                                  (:predicate decision-function-mappings))]
  (fn [type symbol]

    )))
