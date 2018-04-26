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
  ;TODO: load screen black to start
  (let [set-fade #(if (:fade? %)
                      (assoc % :alpha
                            (- (:alpha %) (:fade-increment %))) %)
                                ;TODO: start-delay not updating
        update-start-delay #(if (:start-delay %)
                              (let [current-delay (:start-delay %)]
                                (if (>= 0 current-delay)
                                  (assoc % :fade? true)
                                  (assoc % :start-delay (- current-delay 1)))) %)
        make-updates #(set-fade (update-start-delay %))]
    (if (list? layers)
      (doall (map make-updates layers))
      (make-updates layers))))

(defn draw-static-screen-from-preset
  "take loaded preset(s), draw"
  [presets gr]
  ;(println presets)
  (let [process-fn (fn [layer gr]
        (if (:draw? layer)
          (if (:fade? layer)
              (do (draw-screen-alpha layer gr) layer)
              (do (utils/draw-image (:image layer) gr 0 0) layer))
                layer))]
  (if (list? presets)
      (doall (map #(process-fn % gr) presets))
      (do (process-fn presets gr)))))
