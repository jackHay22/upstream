(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.utilities.images :as images] ;remove
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.utilities.save :as save]
            [upstream.tilemap.chunkutility :as chunk-reload]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def tile-resource (atom nil))
(def player-input-map (atom {:update-facing :south :update-action :at-rest}))
(def entity-state (atom '()))
(def test-image (images/load-image "tiles/unit_bright.png"))

(defn init-level-one
  "load resources, return draw safe state pipeline"
  []
  (do
    (reset! chunk-reload/chunk-store-loaded? false) ;note: map hotswapping should be done with the autosaver off --> remove in prod
    (reset! tile-resource (tile-manager/load-tile-resource config/LEVEL-ONE-TILEMAPS))

    ;(save/start-autosaver entity-state) ; --> add in prod
    ;returns state entity-state-pipeline
    (entity-manager/load-entities
            (save/load-from-save config/LEVEL-ONE-ENTITIES))))
      ; (do ;NOTE: this causes a bug (with headless_server check)
      ;   ;server mode (no image load and no autosave) -- Note: if in GP mode
      ;   (reset! entity-state (entity-manager/load-entities config/LEVEL-ONE-ENTITIES)))))

(defn continuous-state-update
  "take entity-state and return update
  state as continuous update for gp"
  [entities]
  (entity-manager/update-entities entities {}))

(defn update-via-server
  "receive state from server rather than internal"
  [server-state]
  ;parse entity state, error check
  )

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
  ; (cond
  ;   (= key :r) (init-level-one) ;--> remove in prod ;this will cause problems if not removed and hotswapping is enabled
  ;   )
  (reset! player-input-map (entity-manager/entitykeypressed key @player-input-map))
  false)

(defn keyreleased-level-one
  "key release handler for level one"
  [key]
  (reset! player-input-map (entity-manager/entitykeyreleased key @player-input-map)))
