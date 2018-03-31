(ns upstream.gamestate.states.levelone
  (:require [upstream.config :as config]
            [upstream.utilities.images :as images]
            [upstream.entities.entitymanager :as entity-manager]
            [upstream.entities.entitypreset :as entity-preset]
            [upstream.tilemap.tiles :as tiles])
  (:gen-class))

(def game-state (atom config/STARTING-GAME-STATE))
(def example-player (atom 0))
(def tile-map-layers (atom 0))
(def this-x (atom 400))
(def this-y (atom 250))
(def entity-state (atom '()))

(defn init-level-one
  "load resources"
  []
  ;TODO: configure for server mode
  (reset! entity-state (list entity-preset/player-preset-1))
  (reset! example-player (images/load-image-scale-by-factor "entities/logger_1.png" @config/COMPUTED-SCALE))
  (reset! tile-map-layers
    (doall (map #(tiles/init-tile-map %) config/LEVEL-ONE-TILEMAPS))))

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
    (reset! this-x (+ current-x 1))

    (reset! tile-map-layers
      (doall (map #(tiles/set-position
                    @this-x
                    @this-y %)
       @tile-map-layers)))
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
  (let [temp-handler-set (list {:y 5 :fn #(println "handler 1")} {:y 10 :fn #(println "handler 2")})
        tilemaps (map #(if (:entity-handler? %) (assoc % :entity-handlers temp-handler-set) %) @tile-map-layers)
        ]
  (doall (map #(tiles/render-map gr %) tilemaps))
    (images/draw-image @example-player gr
      (+ @this-x (:map-offset-x (first tilemaps)))
      (+ @this-y (:map-offset-y (first tilemaps))))
  ))

(defn keypressed-level-one
  "key press handler for level one"
  [key]

  )
;
(defn keyreleased-level-one
  "key release handler for level one"
  [key]

  )
