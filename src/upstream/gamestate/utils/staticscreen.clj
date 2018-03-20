(ns upstream.gamestate.utils.staticscreen
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(def img-list (atom '()))
(def img-alpha (atom 0))
(def alpha-inc (atom 0))
(def fade? (atom false))

(defn register-screen-image
  "register image"
  [new register-fade?]
  (do
    (swap! img-list conj new)
    (if register-fade? (reset! fade? true))))

(defn clear-registered
  "clear registered screen images"
  []
  (reset! img-list '()))

(defn register-fade-increment
  "update screen"
  [inc]
  (reset! alpha-inc inc))

(defn draw-screen-alpha
  "draw screen with alpha"
  [layer gr]
  (let [update-a (+ @img-alpha @alpha-inc)]
    (do
      (reset! img-alpha update-a)
      (utils/draw-image-alpha layer gr 0 0 update-a))))

(defn draw-screen
  "draw image"
  [gr]
  (let [layer-list @img-list]
    (if (not (empty? layer-list))
      (doseq [layer layer-list]
        (if @fade?
          (draw-screen-alpha layer gr)
          (utils/draw-image layer gr 0 0))))))
