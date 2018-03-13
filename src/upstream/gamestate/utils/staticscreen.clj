(ns upstream.gamestate.utils.staticscreen
  (:require [seesaw.graphics :as sawgr])
  (:gen-class))

(def img (atom 0))

(defn register-screen-image
  "register image"
  [new]
  (reset! img new))

(defn keypressed-load
  [key])

(defn keyreleased-load
  [key])

(defn draw-screen
  "draw image"
  [gr]
  (sawgr/draw gr
      (sawgr/image-shape 0 0 (deref img)) (sawgr/style)))
