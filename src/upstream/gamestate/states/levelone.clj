(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.utilities.save :as save]
            [upstream.tilemap.chunkutility :as chunk-reload]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def tile-resource (atom nil))
(def player-input-map (atom {:update-facing :south :update-action :at-rest}))

(defn init-actual-state
  "load actual gamestate information"
  []
  (entity-manager/load-entities
          (save/load-from-save config/LEVEL-ONE-ENTITIES)))

(defn init-level-one
  "load resources, return draw safe state pipeline"
  []
  (do (reset! tile-resource (tile-manager/load-tile-resource config/LEVEL-ONE-TILEMAPS))
      (init-actual-state)))

(defn update-with-input
  "make updates with control map and state"
  [state-pipeline control-maps]
  (entity-manager/update-entities state-pipeline control-maps))

(defn update-level-one
  "update level1, return pipeline"
  [entity-state-pipeline]
  (update-with-input entity-state-pipeline @player-input-map))

(defn draw-level-one
  "update and draw handler for level one"
  [gr entity-state-pipeline]
  (tile-manager/render-map
              gr (entity-manager/get-central-render-map entity-state-pipeline)
              @tile-resource (entity-manager/create-draw-handlers entity-state-pipeline)))

(defn keypressed-level-one
  "key press handler for level one"
  [key]
  (reset! player-input-map (entity-manager/entitykeypressed key @player-input-map))
  false)

(defn keyreleased-level-one
  "key release handler for level one"
  [key]
  (reset! player-input-map (entity-manager/entitykeyreleased key @player-input-map)))

(defn get-input-map
  "return local input map"
  [] @player-input-map)
