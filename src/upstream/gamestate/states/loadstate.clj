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
  (do
    (reset! static-screen
      {:image (util/load-image-scale-by-width
                  "menus/temp_splash3.png" @config/WINDOW-WIDTH)
      :fade? false
      :start-delay (/ config/LOAD-SCREEN-TTL 2)
      :draw? true
      :alpha 1
      :fade-increment (/ 1.0 (/ @ttl config/LOAD-SCREEN-FADE-DIVISION))}))))

(defn draw-load
  "update and draw handler for load"
  [gr]
  (screen/draw-static-screen-from-preset @static-screen gr))

(defn update-load
  "update load state"
  []
  (do
    ;(println @static-screen)
    (reset! static-screen (screen/update-alpha-layers @static-screen))
    (swap! ttl dec)
    (> @ttl 0)))

(defn keypressed-load
  "key press handler for load"
  [key])

(defn keyreleased-load
  "key release handler for load"
  [key])
