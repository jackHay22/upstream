(ns upstream.entities.entitydecisionmanager
  (:require [upstream.config :as config]
            [upstream.entities.decisionlib :as decisionlib]
            [clojure.java.io :as io]
            [upstream.utilities.log :as log])
  (:gen-class))

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

(defn make-player-decision
  "make decision at update"
  [entity]
  (let [loaded-decisions (:decisions entity)]
  ; return movement map (sort of like player input)
  {:update-facing :south :update-action :at-rest}))
