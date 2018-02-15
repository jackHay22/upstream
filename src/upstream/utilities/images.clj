(ns upstream.utilities.images
  (:require
    [seesaw.graphics :as sawgr]
    [seesaw.icon :as sawicon])
  (:gen-class))

(defn load-image
    "load an image from resources"
    [loc]
    (sawicon/icon
      (javax.imageio.ImageIO/read
          (clojure.java.io/resource loc))))
