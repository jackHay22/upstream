(ns upstream.entities.entitydecisionmanager
  (:require [upstream.config :as config]
            [upstream.entities.decisionlib :as decisionlib]
            [clojure.java.io :as io]
            [upstream.utilities.log :as log])
  (:gen-class))

(def standard-result-map {:update-facing nil :update-action nil})

(defn append-lookup-symbols
  "add lookup args to statement lines"
  [loaded-statements]
  ;TODO
  (map (fn [statement-line] ) loaded-statements))

(defn load-entity-decisions
  "load decisions from file"
  [file]
  (if file
    ;TODO: add prefixes to loaded decisions (predicates and actions)
    (append-lookup-symbols
        (with-open [reader (clojure.java.io/reader (io/resource file))]
            (map (fn [instr-pair]
                      (map (fn [instr]
                        (map #(read-string %) (clojure.string/split instr #" "))) instr-pair))
                (map (fn [line] (clojure.string/split line #" | "))
                    (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader)))))))
    false))

(defn evaluate-actions
  "perform action and return result"
  [actions entity-context]
  (reduce #(%2 %1 entity-context) standard-result-map
          (map #(decisionlib/get-decision-function-cached :action %) actions)))

(defn evaluate-predicates
  "take predicates and evaluate"
  [preds entity-context]
  (let [resolve-symbols (map #(decisionlib/get-decision-function-cached :predicate %) preds)]
      (reduce #((first resolve-symbols) %1 (%2 entity-context)) true (rest resolve-symbols))))

(defn make-player-decision
  "take loaded decisions and operate on first applicable
  --format: ((and/or & :preds) (& :actions))
  --context has :all-positions"
  [entity-context]
  (evaluate-actions
      (reduce (fn [res statement] (if (evaluate-predicates (first statement) entity-context)
                                      (reduced (second statement)) res))
      false (:decisions entity-context)) entity-context))
