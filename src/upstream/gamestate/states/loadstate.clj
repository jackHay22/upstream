(ns upstream.gamestate.states.loadstate
  (:require [upstream.gamestate.utils.staticscreen :as screen]
            [upstream.utilities.images :as util])
  (:gen-class))

(def ttl (atom 100))

(defn init-load
  "perform all necessary resource loads"
  []
  (screen/register-screen-image (util/load-image "menus/temp_splash2.png")))

(defn draw-load
  "update and draw handler for load"
  [gr]
  (screen/draw-screen gr))

(defn update-load
  "update load state"
  []
  (let [current-ttl (deref ttl)]
    (if (> current-ttl 0)
      (do (swap! ttl dec) true)
      false)))

(defn keypressed-load
  "key press handler for load"
  [key]

  )
;
(defn keyreleased-load
  "key release handler for load"
  [key]

  )
