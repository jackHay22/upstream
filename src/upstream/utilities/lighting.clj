(ns upstream.utilities.lighting
  (:require [upstream.config :as config]
            [upstream.tilemap.tilepreset :as preset])
  (:gen-class))

(import java.awt.geom.Point2D)
(import java.awt.Color)
(import java.awt.RadialGradientPaint)
(import java.awt.Graphics2D)
(import java.awt.AlphaComposite)
(import java.awt.image.BufferedImage)
(import java.awt.MultipleGradientPaint)

(defn get-lighting-profile
  "returns color given a tile and a layer"
  [layer x y] ;x and y are map relative player positions
  ((layer preset/lighting-preset) x y))

(defn render-lighting
  "render lighting at point"
  [gr x y layer]
  (let [win-width @config/WINDOW-RESOURCE-WIDTH
        win-height @config/WINDOW-RESOURCE-HEIGHT
        lighting-layer (BufferedImage. win-width win-height BufferedImage/TYPE_INT_ARGB)
        g2d (cast Graphics2D (.createGraphics lighting-layer))
        dist (float-array [0.1 1.0])
        lighting-profile (get-lighting-profile layer x y)
        radial-color (into-array Color [(Color. 0.0 0.0 0.0 0.0) (:color lighting-profile)])
        gradient (RadialGradientPaint. (float x) (float y) (:radius lighting-profile) dist radial-color)]
        (do
          (.setPaint g2d gradient)
          (.setComposite g2d (AlphaComposite/getInstance AlphaComposite/SRC_OVER 0.95))
          (.fillRect g2d 0 0 win-width win-height)
          (.drawImage gr lighting-layer 0 0 win-width win-height nil)
          (.dispose g2d))))

(defn cast-shadow
  "draw shadow at centered pt and width"
  [gr x y width]
  (let [win-width @config/WINDOW-RESOURCE-WIDTH
        win-height @config/WINDOW-RESOURCE-HEIGHT
        perspective-bounds (java.awt.geom.Rectangle2D$Float. (float x) (float y) (float width) (float (/ width 2)))
        shadow-layer (BufferedImage. win-width win-height BufferedImage/TYPE_INT_ARGB)
        g2d (cast Graphics2D (.createGraphics shadow-layer))
        dist (float-array [0.1 1.0])
        radial-color (into-array Color [(Color. 0 0 0 100) (Color. 0.0 0.0 0.0 0.0)])
        gradient (RadialGradientPaint. perspective-bounds dist radial-color
                        java.awt.MultipleGradientPaint$CycleMethod/NO_CYCLE)]
    (do
      (.setPaint g2d gradient)
      (.setComposite g2d (AlphaComposite/getInstance AlphaComposite/SRC_OVER 0.95))
      (.fillRect g2d 0 0 win-width win-height)
      (.drawImage gr shadow-layer 0 0 win-width win-height nil)
      (.dispose g2d))))
