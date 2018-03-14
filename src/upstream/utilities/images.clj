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

(defn load-image-scale-by-width
  "take image, rescale by new x"
  [image new-w]
  (let [loaded (javax.imageio.ImageIO/read
                  (clojure.java.io/resource image))
        current-w (.getWidth loaded)
        current-h (.getHeight loaded)
        new-h (* new-w (/ current-h current-w))]
        (sawicon/icon (.getScaledInstance loaded new-w new-h Image/SCALE_DEFAULT))))

(defn draw-image
  "take image, gr, x,y, draw"
  [img gr x y]
  (sawgr/draw gr
    (sawgr/image-shape x y img) (sawgr/style)))
