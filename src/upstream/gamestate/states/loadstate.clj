(ns upstream.gamestate.states.loadstate
  (:require [upstream.gamestate.utils.staticscreen :as screen]
            [upstream.config :as config]
            [upstream.utilities.images :as util])
  (:gen-class))

(def ttl (atom config/LOAD-SCREEN-TTL))

(defn init-load
  "perform all necessary resource loads"
  []
  (screen/register-screen-image
    {:image (util/load-image-scale-by-width
      "menus/temp_splash3.png" @config/WINDOW-WIDTH) :fade? true})
  (screen/register-fade-increment (/ 1.0 (/ @ttl config/LOAD-SCREEN-FADE-DIVISION))))

(defn draw-load
  "update and draw handler for load"
  [gr]
  (screen/draw-screen gr))

(defn update-load
  "update load state"
  []
  (let [current-ttl (deref ttl)]
    (if (> current-ttl 0)
      (do
        (swap! ttl dec)
        (if (and
              (>= (/ config/LOAD-SCREEN-TTL 4) current-ttl)
              (not (screen/fade-started?)))
            (screen/start-screen-fade))
        true) false)))

(defn keypressed-load
  "key press handler for load"
  [key])

(defn keyreleased-load
  "key release handler for load"
  [key])
