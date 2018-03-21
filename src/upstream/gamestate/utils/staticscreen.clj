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
    (swap! img-list conj (assoc new :draw? true)))

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
      (if (and (>= update-a 0) (<= update-a 1))
        (do
          (reset! img-alpha update-a)
          (utils/draw-image-alpha (:image layer) gr 0 0 update-a)
          layer)
        (assoc layer :draw? false))))

(defn draw-screen
  "draw image"
  [gr]
  (let [layer-list @img-list]
    (if (not (empty? layer-list))
      (reset! img-list (doall
        (map (fn [layer]
          (if (:draw? layer)
            (if (:fade? layer)
              (draw-screen-alpha layer gr)
            (do
              (utils/draw-image (:image layer) gr 0 0)
              layer)))) layer-list))))))
