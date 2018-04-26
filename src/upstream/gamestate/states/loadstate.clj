(ns upstream.gamestate.states.loadstate
  (:require [upstream.gamestate.utils.staticscreen :as screen]
            [upstream.config :as config]
            [upstream.utilities.images :as util])
  (:gen-class))

(def ttl (atom config/LOAD-SCREEN-TTL))
(def static-screen (atom {}))

(defn init-load
  "perform all necessary resource loads"
  []
  (if (not @config/HEADLESS-SERVER?)
      {:image (util/load-image "menus/temp_splash3.png")
      :fade? false
      :start-delay (/ config/LOAD-SCREEN-TTL 2)
      :draw? true
      :alpha 1
      :fade-increment (/ 1.0 (/ @ttl config/LOAD-SCREEN-FADE-DIVISION))}
    nil))

(defn draw-load
  "update and draw handler for load"
  [gr state-pipeline]
  (screen/draw-static-screen-from-preset state-pipeline gr))

(defn update-load
  "update load state"
  [state-pipeline]
  (if (> @ttl 0)
      (do
          (swap! ttl dec)
          (screen/update-alpha-layers state-pipeline))
      nil))

(defn keypressed-load
  "key press handler for load"
  [key])

(defn keyreleased-load
  "key release handler for load"
  [key])
