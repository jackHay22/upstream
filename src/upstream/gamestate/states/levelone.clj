(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.tilemap.chunkutility :as chunk-reload]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def tile-resource (atom nil))
(def entity-images-resource (atom nil))

(defn init-actual-state
  "load actual gamestate information"
  [base-state]
  ;Important note: for local gameplay, the main player MUST be first in state list
  (entity-manager/load-entities base-state))

(defn init-level-one
  "load resources, return draw safe state pipeline"
  [base]
  (do (reset! tile-resource (tile-manager/load-tile-resource config/LEVEL-ONE-TILEMAPS))
      (reset! entity-images-resource (entity-manager/load-entity-image-resources base))
      (init-actual-state base)))

(defn update-level-one
  "make updates with control map and state"
  [entity-state-pipeline graphics?]
  (entity-manager/update-entities entity-state-pipeline graphics?))

(defn draw-level-one
  "update and draw handler for level one"
  [gr entity-state-pipeline]
  (tile-manager/render-map
              gr (entity-manager/get-central-render-map entity-state-pipeline)
              @tile-resource
              (entity-manager/create-draw-handlers
                entity-state-pipeline @entity-images-resource)))

(defn keypressed-level-one
  "key press handler for level one"
  [key entity-state-pipeline]
  (update-in (into [] entity-state-pipeline) [0 :control-input]
          #(entity-manager/entitykeypressed key %)))

(defn keyreleased-level-one
  "key release handler for level one"
  [key entity-state-pipeline] ;TODO: potentially needs optimization with large numbers of entities (into vector)
  (update-in (into [] entity-state-pipeline) [0 :control-input]
          #(entity-manager/entitykeyreleased key %)))
