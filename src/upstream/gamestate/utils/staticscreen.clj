(ns upstream.gamestate.utils.staticscreen
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(def img-list (atom '()))
(def img-alpha (atom 1))
(def alpha-inc (atom 0))
(def fade? (atom false))

(defn register-screen-image
  "register image"
  [new]
    (swap! img-list conj new))

(defn start-screen-fade
  "start to fade screen"
  []
  (reset! fade? true))

(defn fade-started? [] @fade?)

(defn clear-registered
  "clear registered screen images"
  []
  (do
    (reset! img-list '())
    (reset! fade? false)
    (reset! img-alpha 1)))

(defn register-fade-increment
  "update screen"
  [inc initial]
  (do
    (reset! alpha-inc inc)
    (reset! img-alpha initial)))

(defn draw-screen-alpha
  "draw screen with alpha"
  [layer gr]
  (let [update-a (- @img-alpha @alpha-inc)]
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
