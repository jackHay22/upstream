(ns upstream.gamestate.states.levelone
  (:require [upstream.engine.config :as config]
            [upstream.tilemap.tiles :as tiles])
  (:gen-class))

(def game-state (atom config/STARTING-STATE))
(def tile-map-layers (atom 0))
(def this-x (atom 50))
(def this-y (atom 50))

(defn init-level-one
  "load resources"
  []
  (reset! tile-map-layers
  (tiles/init-tile-map "maps/basic_template.txt"
                 "tiles/unit_blank.png"
                 64 config/TILES-ACROSS :image :sound)
  ))

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

    (reset! tile-map-layers (tiles/set-position @this-x @this-y @tile-map-layers))
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
  ;(println @tile-map-layers)
  (tiles/render-map gr @tile-map-layers 0)
  ; (doall ) (tiles/render-map %) @tile-map-layers
  )

(defn keypressed-level-one
  "key press handler for level one"
  [key]

  )
;
(defn keyreleased-level-one
  "key release handler for level one"
  [key]

  )
