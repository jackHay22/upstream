(ns upstream.entities.entitydecisionmanager
  (:require [upstream.config :as config]
            [upstream.entities.decisionlib :as decisionlib]
            [clojure.java.io :as io]
            [upstream.utilities.log :as log])
  (:gen-class))

(def result-map-default {:update-facing :south :update-action :at-rest})

(defn load-entity-decisions
  "load decisions from file"
  [file]
  (if file
    (with-open [reader (clojure.java.io/reader (io/resource file))]
            (map (fn [instr-pair]
                      (map (fn [instr]
                        (map #(decisionlib/resolve-loaded-name (read-string %)) (clojure.string/split instr #" "))) instr-pair))
                (map (fn [line] (clojure.string/split line #" : "))
                    (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader))))))
    false))

(defn make-player-decision
  "make the first possible operation and return control structure"
  [entity-context]
  (let [loaded-decisions (:decisions entity-context)]
      (reduce #(if (decisionlib/evaluate-predicates (first %2))
                      (reduced (:control-input (decisionlib/evaluate-actions (second %2))))
                      %1) result-map-default loaded-decisions)))
