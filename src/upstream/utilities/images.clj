(ns upstream.utilities.images
  (:require
    [seesaw.graphics :as sawgr]
    [seesaw.icon :as sawicon])
  (:gen-class))

(import java.awt.Image)

(defn load-image
    "load an image from resources"
    [loc]
    (sawicon/icon
      (javax.imageio.ImageIO/read
          (clojure.java.io/resource loc))))



(defn scale-loaded-instance
  "return scaled version of image"
  [image new-h new-w]
  (.getScaledInstance image new-h new-w Image/SCALE_DEFAULT))

(defn draw-image
  "take image, gr, x,y, draw"
  [img gr x y]
  (sawgr/draw gr
    (sawgr/image-shape x y img) (sawgr/style)))
