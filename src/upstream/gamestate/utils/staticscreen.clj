(ns upstream.gamestate.utils.staticscreen
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(defn draw-screen-alpha
  "draw screen with alpha"
  [layer gr]
    (let [a-val (:alpha layer)]
      (if (and (>= a-val 0) (<= a-val 1))
        (do
          (utils/draw-image-alpha (:image layer) gr 0 0 a-val)
          (assoc layer :alpha (- a-val (:fade-increment layer))))
        (assoc layer :draw? false))))

(defn update-alpha-layers
  "update alpha changes"
  [layers]
  (let [set-fade #(if (:fade? %)
                          (let [layer-alpha (:alpha %)]
                                (assoc % :alpha
                                  (- layer-alpha (:fade-increment %)))) %)]
    (if (list? layers)
      (doall (map set-fade layers))
      (set-fade layers))))

(defn draw-static-screen-from-preset
  "take loaded preset(s), draw"
  [presets gr]
  (let [process-fn (fn [layer gr]
        (if (:draw? layer)
          (if (:fade? layer)
              (draw-screen-alpha layer gr)
              (do
                ;TODO: why are non fading layers not drawing?
                (utils/draw-image (:image layer) gr 0 0) layer))
                layer))]

  (if (list? presets)
      (doall (map #(process-fn % gr) presets))
      (do (process-fn presets gr)))))
