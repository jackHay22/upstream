(ns upstream.utilities.images
  (:gen-class))

(import java.awt.Image)
(import java.awt.AlphaComposite)
(import java.awt.image.RescaleOp)
(import java.awt.image.BufferedImage)

(defn sub-image-loader
  "gets a subimage the size of a tile from the big image"
  [loc]
  ;only perform load once, returns fn
  (let [loaded-resource (javax.imageio.ImageIO/read
                          (clojure.java.io/resource loc))]
    {:resource-width (.getWidth loaded-resource)
     :resource-height (.getHeight loaded-resource)
     :load-fn (fn [x y w h] ;converted to seesaw icon when scaled
        (.getSubimage loaded-resource x y w h))}))

(defn scale-loaded-image-by-width
  "scale loaded image, wrap with icon"
  [img new-w]
    (.getScaledInstance img
      new-w (* new-w (/ (.getHeight img) (.getWidth img)))
      Image/SCALE_DEFAULT))

(defn scale-loaded-image-by-factor
  "take image, rescale by new x"
  [img scale]
    (.getScaledInstance img
      (* scale (.getWidth img))
      (* scale (.getHeight img))
      Image/SCALE_DEFAULT))

(defn load-image
    "load an image from resources"
    [loc]
      (javax.imageio.ImageIO/read
          (clojure.java.io/resource loc)))

(defn get-resource-dim
  "take resouce and get tuple of w,h"
  [path]
  (let [resource (javax.imageio.ImageIO/read
                  (clojure.java.io/resource path))]
      (list (.getWidth resource) (.getHeight resource))))

(defn load-image-scale-by-width
  "take image, rescale by new x"
  [image new-w]
  (let [loaded (javax.imageio.ImageIO/read
                  (clojure.java.io/resource image))
        current-w (.getWidth loaded)
        current-h (.getHeight loaded)
        new-h (* new-w (/ current-h current-w))]
        (.getScaledInstance loaded new-w new-h Image/SCALE_DEFAULT)))

(defn load-image-scale-by-factor
  "load a raw image by a set scale"
  [image scale]
  (let [loaded (javax.imageio.ImageIO/read
                  (clojure.java.io/resource image))
        w (* scale (.getWidth loaded))
        h (* scale (.getHeight loaded))]
    (.getScaledInstance loaded w h Image/SCALE_DEFAULT)))

(defn draw-image
  "draw an image to the gr object"
  [img gr x y]
  (try
    (.drawImage gr img x y nil)
    (catch Exception e (println "Upstream => Failed to render image:" img))))

(defn draw-image-alpha
  "take image, gr, x, y, alpha value, draw"
  [img gr x y a]
  ;TODO: fix
  ; (let [alpha-fn #(.setComposite gr
  ;                   (AlphaComposite/getInstance AlphaComposite/SRC_OVER %))]
  ;   (do
  ;     (alpha-fn (min (max a 0) 1))
      (draw-image img gr x y)
      )
      ;(alpha-fn 1))))

(defn draw-images-brightness
  "gr, brightness value, return brightness draw fn"
  [gr b]
  (let [bfilter (RescaleOp. (float b) 0.0 nil)]
        (fn [img x y]
            (let [filtered-image (.filter bfilter img nil)]
            (draw-image filtered-image gr x y)))))
