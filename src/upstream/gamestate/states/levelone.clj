(ns upstream.gamestate.states.levelone
  (:require [upstream.engine.config :as config]
            [upstream.tilemap.tiles :as tiles])
  (:gen-class))

(def game-state (atom config/STARTING-STATE)) ;hmmm

(defn init-level-one
  "load resources"
  []
  )

(defn update-via-server
  "receive state from server rather than internal"
  [server-state]
  (reset! game-state server-state))

(defn update-level-one
  "update"
  []
  (let [state @game-state]
  true))

(defn draw-level-one
  "update and draw handler for level one"
  [gr]
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
