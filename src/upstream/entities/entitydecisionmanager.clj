(ns upstream.entities.entitydecisionmanager
  (:require [upstream.config :as config])
  (:gen-class))

(defn load-entity-decisions
  "load decisions from file"
  [file])

(defn make-player-decision
  "make decision at update"
  [entity]
  ; return movement map (sort of like player input)
  {:update-facing :south :update-action :at-rest})
