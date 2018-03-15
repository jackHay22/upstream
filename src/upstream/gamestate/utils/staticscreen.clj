(ns upstream.gamestate.utils.staticscreen
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(def img-list (atom '()))

(defn register-screen-image
  "register image"
  [new]
    (swap! img-list conj new))

(defn clear-registered
  "clear registered screen images"
  []
  (reset! img-list '()))

(defn draw-screen
  "draw image"
  [gr]
  (let [layer-list @img-list]
    (if (not (empty? layer-list))
      (doseq [layer layer-list]
        (utils/draw-image layer gr 0 0)))))
