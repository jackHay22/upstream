(ns upstream.gamestate.utils.paralax
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(def paralax-state (atom '()))

(defn register-layers
  "take list of maps that include loaded images and relative tick speed"
  [layer-maps width]
  (reset! paralax-state
    (map #(assoc % :width width)
        (map #(assoc % :x 10) layer-maps))))

(defn update-layers
  "update all registered layers"
  []
  (let [layers @paralax-state]
    (reset! paralax-state
      (map #(assoc % :x
        (if (> (:x %) (:width %)) 0 (+ (:x %) (:dx %)))) layers))))

(defn render-layers
  "render all registered layers (twice)"
  [gr]
  (doseq [layer @paralax-state]
    (utils/draw-image (:image layer) gr (:x layer) 0)
    (utils/draw-image (:image layer) gr (- (:x layer) (:width layer)) 0)))
