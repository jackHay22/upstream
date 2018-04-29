(ns upstream.utilities.lighting
  (:require [upstream.config :as config])
  (:gen-class))

(import java.awt.geom.Point2D)
(import java.awt.Color)
(import java.awt.RadialGradientPaint)
(import java.awt.Graphics2D)
(import java.awt.AlphaComposite)

(defn render-lighting
  "render lighting at point"
  [gr x y radius]
  (let [g2d (cast Graphics2D gr)
        dist (float-array [0.1 1.0])
        radial-color (into-array Color [(Color. 0.0 0.0 0.0 0.0) (Color. 55 65 64 200)])
        gradient (RadialGradientPaint. (float x) (float y) (float radius) dist radial-color)]
        (.setPaint g2d gradient)
        (.setComposite g2d (AlphaComposite/getInstance AlphaComposite/SRC_OVER 0.95))
        (.fillRect g2d 0 0 @config/WINDOW-RESOURCE-WIDTH @config/WINDOW-RESOURCE-HEIGHT)
        (.dispose g2d)))
