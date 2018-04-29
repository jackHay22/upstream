(ns upstream.utilities.lighting
  (:require [upstream.config :as config]
            [upstream.tilemap.tilepreset :as preset])
  (:gen-class))

(import java.awt.geom.Point2D)
(import java.awt.Color)
(import java.awt.RadialGradientPaint)
(import java.awt.Graphics2D)
(import java.awt.AlphaComposite)

(defn get-lighting-profile
  "returns color given a tile and a layer"
  [layer x y] ;x and y are map relative player positions
  ((layer preset/lighting-preset) x y))

(defn render-lighting
  "render lighting at point"
  [gr x y layer]
  (let [g2d (cast Graphics2D gr)
        dist (float-array [0.1 1.0])
        lighting-profile (get-lighting-profile layer x y)
        radial-color (into-array Color [(Color. 0.0 0.0 0.0 0.0) (:color lighting-profile)])
        gradient (RadialGradientPaint. (float x) (float y) (:radius lighting-profile) dist radial-color)]
        (.setPaint g2d gradient)
        (.setComposite g2d (AlphaComposite/getInstance AlphaComposite/SRC_OVER 0.95))
        (.fillRect g2d 0 0 @config/WINDOW-RESOURCE-WIDTH @config/WINDOW-RESOURCE-HEIGHT)))
        ;TODO: confirm that not disposing g2d is okay
