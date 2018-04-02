(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.utilities.images :as images]
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.tilemap.tilemanager :as tile-manager])
  (:gen-class))

(def game-state (atom config/STARTING-GAME-STATE))
(def example-player (atom 0)) ;remove
(def tile-map-layers (atom '()))
(def player-input-map (atom {}))
(def this-x (atom 400)) ;remove
(def this-y (atom 250)) ;remove
(def entity-state (atom '()))

(defn init-level-one
  "load resources"
  []
  ;TODO: configure for server mode
  (reset! example-player (images/load-image-scale-by-factor "entities/logger_1.png" @config/COMPUTED-SCALE))
  (reset! tile-map-layers
    (tile-manager/load-tile-maps config/LEVEL-ONE-TILEMAPS))
  (reset! entity-state
    (entity-manager/load-entities config/LEVEL-ONE-ENTITIES)))

(defn update-via-server
  "receive state from server rather than internal"
  [server-state]
  (reset! game-state server-state))

(defn update-level-one
  "update"
  []
  ;entities: create overlap handler with subscribers?, send to tilemap at render
  (let [state @game-state
        current-x @this-x]
    ;(reset! this-x (+ current-x 1))

     (reset! tile-map-layers
       (doall (map #(tile-manager/set-position
                     @this-x
                     @this-y %)
        @tile-map-layers)))
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
  (let [temp-handler-set (list {:y 5 :fn #(println "handler 1")} {:y 10 :fn #(println "handler 2")})
        tilemaps (map #(if (:entity-handler? %) (assoc % :entity-handlers temp-handler-set) %) @tile-map-layers) ;get from entity manager layers
        ]
  (doall (map #(tile-manager/render-map gr %) tilemaps))
    (images/draw-image @example-player gr
      (+ @this-x (:map-offset-x (first tilemaps)))
      (+ @this-y (:map-offset-y (first tilemaps))))
  ))

(defn keypressed-level-one
  "key press handler for level one"
  [key]
  (cond
    (= key :r)
    ;allow tilemap reload (dev mode)
        (init-level-one))
  (reset! player-input-map (entity-manager/entitykeypressed key @player-input-map))
  false)

(defn keyreleased-level-one
  "key release handler for level one"
  [key]
  (reset! player-input-map (entity-manager/entitykeyreleased key @player-input-map)))
