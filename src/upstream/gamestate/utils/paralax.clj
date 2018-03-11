(ns upstream.gamestate.utils.paralax
  (:require
    [seesaw.graphics :as sawgr]
    [seesaw.icon :as sawicon])
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
  (let [layers (deref paralax-state)]
    (reset! paralax-state
      (map #(assoc % :x
        (if (> (:x %) (:width %)) 0 (+ (:x %) (:dx %)))) layers))))

(defn render-layers
  "render all registered layers (twice)"
  [gr]
  (doseq [layer (deref paralax-state)]
    (sawgr/draw gr
      (sawgr/image-shape (:x layer) 0 (:image layer)) (sawgr/style))
    (sawgr/draw gr
      (sawgr/image-shape (- (:x layer) (:width layer)) 0 (:image layer)) (sawgr/style))))
