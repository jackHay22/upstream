(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.utilities.images :as images] ;remove
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.utilities.save :as save]
            [upstream.tilemap.chunkutility :as chunk-reload]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def tile-resource (atom nil))
(def player-input-map (atom {}))
(def entity-state (atom '()))

(defn init-level-one
  "load resources"
  []
  (reset! chunk-reload/chunk-store-loaded? false) ;note: map hotswapping should be done with the autosaver off --> remove in prod
  (reset! tile-resource (tile-manager/load-tile-resource config/LEVEL-ONE-TILEMAPS))
  (reset! entity-state (entity-manager/load-entities
                                (save/load-from-save config/LEVEL-ONE-ENTITIES)))
  ;(save/start-autosaver entity-state) ; --> add in prod
  )

(defn update-via-server
  "receive state from server rather than internal"
  [server-state]
  ;parse entity state, error check
  )

(defn update-level-one
  "update level1"
  []
  (let [current-entity-state @entity-state]
       (reset! entity-state (entity-manager/update-entities current-entity-state @player-input-map))
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
  (let [entity-set @entity-state]
  (tile-manager/render-map
              gr (entity-manager/get-central-render-map entity-set)
              @tile-resource (entity-manager/create-draw-handlers entity-set 32)))) ;TODO update hardcoded grid size

(defn keypressed-level-one
  "key press handler for level one"
  [key]
  (cond
    (= key :r) (init-level-one) ;--> remove in prod ;this will cause problems if not removed and hotswapping is enabled
    (= key :s) (save/save-state @entity-state))
  (reset! player-input-map (entity-manager/entitykeypressed key @player-input-map))
  false)

(defn keyreleased-level-one
  "key release handler for level one"
  [key]
  (reset! player-input-map (entity-manager/entitykeyreleased key @player-input-map)))
