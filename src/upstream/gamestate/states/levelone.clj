(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.utilities.save :as save]
            [upstream.tilemap.chunkutility :as chunk-reload]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def tile-resource (atom nil))

(defn init-actual-state
  "load actual gamestate information"
  []
  ;Important note: for local gameplay, the main player MUST be first in state list
  (entity-manager/load-entities
          (save/load-from-save config/LEVEL-ONE-ENTITIES)))

(defn init-level-one
  "load resources, return draw safe state pipeline"
  []
  (do (reset! tile-resource (tile-manager/load-tile-resource config/LEVEL-ONE-TILEMAPS))
      (init-actual-state)))

(defn update-level-one
  "make updates with control map and state"
  [entity-state-pipeline]
  (entity-manager/update-entities entity-state-pipeline true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr entity-state-pipeline]
  (tile-manager/render-map
              gr (entity-manager/get-central-render-map entity-state-pipeline)
              @tile-resource (entity-manager/create-draw-handlers entity-state-pipeline)))

(defn keypressed-level-one
  "key press handler for level one"
  [key entity-state-pipeline]
  (update-in entity-state-pipeline [0 :control-input]
          #(entity-manager/entitykeypressed key %)))

(defn keyreleased-level-one
  "key release handler for level one"
  [key entity-state-pipeline]
  (update-in entity-state-pipeline [0 :control-input]
          #(entity-manager/entitykeyreleased key %)))
