(ns upstream.gamestate.utils.staticscreen
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(def img-list (atom '()))
(def img-alpha (atom 1))
(def alpha-inc (atom 0))
(def fade? (atom false))

(defn register-screen-image
  "register image: takes map with image and :fade? param"
  [new]
    (swap! img-list conj (assoc new :draw? true)))

(defn start-screen-fade
  "start to fade screen"
  [] (reset! fade? true))

(defn fade-started? [] @fade?) ;TODO: use?

(defn clear-registered
  "clear registered screen images"
  []
  (do
    (reset! img-list '())
    (reset! fade? false)
    (reset! img-alpha 1)))

(defn register-fade-increment
  "update screen"
  [inc]
  (do
    (reset! alpha-inc inc)
    (if (> inc 0)
      (reset! img-alpha 1)
      (reset! img-alpha 0))))

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
            (if (and @fade? (:fade? layer))
              (draw-screen-alpha layer gr)
              (do ;TODO: non fade layers not drawing immediately? (side effect problem?)
                (utils/draw-image (:image layer) gr 0 0) layer))
            layer)) layer-list))))))
