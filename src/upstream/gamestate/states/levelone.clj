(ns upstream.gamestate.states.levelone
  (:require [upstream.engine.config :as config]
            [upstream.tilemap.tiles :as tiles])
  (:gen-class))

(def game-state (atom config/STARTING-STATE))
(def tile-map-layers (atom '()))

(defn init-level-one
  "load resources"
  []
  (tiles/init-tile-map "resources/maps/basic_template.txt"
                 "tiles/unit_blank.png"
                 64 config/TILES-ACROSS :image :sound)
  )

(defn update-via-server
  "receive state from server rather than internal"
  [server-state]
  (reset! game-state server-state))

(defn update-level-one
  "update"
  []
  ;entities: create overlap handler with subscribers?, send to tilemap at render
  (let [state @game-state]
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
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
