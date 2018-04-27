(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.utilities.save :as save]
            [upstream.tilemap.chunkutility :as chunk-reload]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def tile-resource (atom nil))
(def player-input-map (atom {:update-facing :south :update-action :at-rest}))

(defn init-level-one
  "load resources, return draw safe state pipeline"
  []
  (do
    (reset! chunk-reload/chunk-store-loaded? false)
    (reset! tile-resource (tile-manager/load-tile-resource config/LEVEL-ONE-TILEMAPS))
    ;returns state entity-state-pipeline
    (entity-manager/load-entities
            (save/load-from-save config/LEVEL-ONE-ENTITIES))))

(defn continuous-state-update
  "take entity-state and return update
  state as continuous update for gp"
  [entities]
  (entity-manager/update-entities entities {}))

(defn update-level-one
  "update level1, return pipeline"
  [entity-state-pipeline]
  (entity-manager/update-entities entity-state-pipeline @player-input-map))

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
