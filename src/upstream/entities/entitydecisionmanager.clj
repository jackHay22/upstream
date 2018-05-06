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

(defn convert-to-positions-in-chunk
  "take player chunk information and filter positions and convert to chunk-relative"
  [entity-context]
  (let [global-positions (:all-positions entity-context)
        corner-chunk (:corner-chunk (first (:current-maps (:map-resource entity-context))))
        chunk-offset-x (:offset-x corner-chunk)
        chunk-offset-y (:offset-y corner-chunk)
        grid-dim (:grid-dim (:map-resource entity-context))
        chunk-dim (:chunk-dim (:map-resource entity-context))]
        (update-in entity-context [:all-positions]
          #(map
            (fn [pt]
              (spacialutility/map-relative-to-chunk-relative
                (first pt) (second pt)
                chunk-offset-x chunk-offset-y grid-dim))
          (filter (fn [global-pt]
                    (spacialutil/grid-pt-in-chunk? global-pt
                        chunk-offset-x chunk-offset-y
                        chunk-dim grid-dim)) global-positions)))))

(defn make-player-decision
  "make the first possible operation and return control structure"
  [entity-context]
  (let [loaded-decisions (:decisions entity-context)
        entity-transform (convert-to-positions-in-chunk entity-context)]
      (reduce #(if (decisionlib/evaluate-predicates (first %2) entity-transform)
                      (reduced (:control-input (decisionlib/evaluate-actions (second %2) entity-transform)))
                      %1) result-map-default loaded-decisions)))
