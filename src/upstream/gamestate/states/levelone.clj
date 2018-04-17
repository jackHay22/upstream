(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.utilities.images :as images] ;remove
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.utilities.save :as save]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def game-state (atom config/STARTING-GAME-STATE))
(def example-player (atom 0)) ;remove
(def tile-resource (atom nil))
(def map-resource (atom nil)) ;TODO: eventually this should be associated with each entity
(def player-input-map (atom {}))
(def this-x (atom 200)) ;remove
(def this-y (atom 200)) ;remove
(def entity-state (atom '()))

(defn init-level-one
  "load resources"
  []
  ;TODO: configure for server mode (TODO: check for server here rather than in each manager)
  (reset! example-player (images/load-image-scale-by-factor "entities/logger_1.png" @config/COMPUTED-SCALE))

  (reset! tile-resource (tile-manager/load-tile-resource config/LEVEL-ONE-TILEMAPS))
  (reset! map-resource (tile-manager/load-map-resource config/LEVEL-ONE-TILEMAPS 200 200))

  (reset! entity-state (entity-manager/load-entities
                                (save/load-from-save config/LEVEL-ONE-ENTITIES)))
  (save/start-autosaver entity-state))

(defn update-via-server
  "receive state from server rather than internal"
  [server-state]
  (reset! game-state server-state))

(defn update-level-one
  "update"
  []
  ;entities: create overlap handler with subscribers?, send to tilemap at render
  (let [state @game-state
        current-x (+ @this-x 0.5)]
     (reset! this-x current-x)
     (reset! map-resource (tile-manager/set-position @this-x @this-y @map-resource))
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
  (let [draw-player-at-offset (fn [gr off-x off-y] (images/draw-image @example-player gr (+ @this-x off-x) (+ @this-y off-y)))
        temp-handler-set (list {:x (int (/ @this-x 32))
                                :y (int (/ @this-y 32))
                                :prevent-block? true
                                :fn (fn [gr o-x o-y] (draw-player-at-offset gr (* (- o-x 10) @config/COMPUTED-SCALE) (* (- o-y 10) @config/COMPUTED-SCALE)))} )
      ]

  (tile-manager/render-map gr @map-resource @tile-resource temp-handler-set)
  ))

(defn keypressed-level-one
  "key press handler for level one"
  [key]
  (cond
    (= key :r) (init-level-one) ;(remove in prod)
    (= key :up) (reset! this-y (- @this-y 1))
    (= key :down) (reset! this-y (+ @this-y 1))
    (= key :left) (reset! this-x (- @this-x 1))
    (= key :right) (reset! this-x (+ @this-x 1))
    (= key :s) (save/save-state @entity-state))
  (reset! player-input-map (entity-manager/entitykeypressed key @player-input-map))
  false)

(defn keyreleased-level-one
  "key release handler for level one"
  [key]
  (reset! player-input-map (entity-manager/entitykeyreleased key @player-input-map)))
