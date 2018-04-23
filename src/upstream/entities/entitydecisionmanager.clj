(ns upstream.entities.entitydecisionmanager
  (:require [upstream.config :as config]
            [upstream.entities.decisionlib :as decisionlib]
            [clojure.java.io :as io]
            [upstream.utilities.log :as log])
  (:gen-class))

(def standard-result-map {:update-facing nil :update-action nil})

(defn load-entity-decisions
  "load decisions from file"
  [file]
  (if file
    ;TODO: add prefixes to loaded decisions (predicates and actions)
    (with-open [reader (clojure.java.io/reader (io/resource file))]
        (map (fn [instr-pair]
                  (map (fn [instr]
                    (map #(read-string %) (clojure.string/split instr #" "))) instr-pair))
            (map (fn [line] (clojure.string/split line #" : "))
                (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader))))))
    false))

(defn evaluate-actions
  "perform action and return result"
  [actions]
  ;TODO verify reduce function works
  (reduce #(%2 %1) standard-result-map (map #(decisionlib/get-decision-function %) actions)))

(defn evaluate-predicates
  "take predicates and evaluate"
  [preds]
  (apply
    (first preds)
    (map #(decisionlib/get-decision-function %) (rest preds))))

(defn make-player-decision
  "take loaded decisions and operate on first applicable
  --format: ((and/or & :preds) (& :actions))"
  [entity]
  (evaluate-actions
      (reduce (fn [res statement] (if (evaluate-predicates (first statement))
                                      (reduced (second statement)) res))
      false (:decisions entity))))
