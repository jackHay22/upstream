(ns upstream.utilities.images
  (:gen-class))

(import java.awt.Image)
(import java.awt.AlphaComposite)
(import java.awt.image.RescaleOp)
(import java.awt.image.BufferedImage)
(import java.awt.geom.AffineTransform)
(import java.awt.image.AffineTransformOp)

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

(defn load-image
    "load an image from resources"
    [loc]
      (javax.imageio.ImageIO/read
          (clojure.java.io/resource loc)))

(defn load-image-scale-by-width
  "take image, rescale by new x"
  [image new-w] ;TODO fix
  (let [loaded (cast BufferedImage (javax.imageio.ImageIO/read
                  (clojure.java.io/resource image)))
                  ]
                  loaded))
                  ;TODO
      ;   current-w (.getWidth loaded)
      ;   current-h (.getHeight loaded)
      ;   scale-factor (/ new-w current-w)
      ;   transform (.scale (AffineTransform.) scale-factor scale-factor)
      ;   operation (AffineTransformOp. transform AffineTransformOp/TYPE_BILINEAR)
      ;   scaled-instance (BufferedImage.
      ;             (* current-w scale-factor) (* current-h scale-factor) BufferedImage/TYPE_INT_ARGB)]
      ; (.filter operation loaded scaled-instance)))

(defn draw-image
  "draw an image to the gr object"
  [img gr x y]
  (try
    (.drawImage gr img x y nil)
    (catch Exception e (println "Upstream => Failed to render image:" img "\n" (.getMessage e)))))

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

(defn draw-image-fade
  ;TODO: not working
  [img gr target fade x y]
    (let [offset (* target (- 1.0 fade))
          offsets (float-array [offset offset offset 0.0])
          scales (float-array [fade fade fade 0.0])
          disp (BufferedImage. (.getWidth img nil) (.getHeight img nil) BufferedImage/TYPE_INT_ARGB)]
          (do
            (.drawImage (.getGraphics disp)  img 0 0 nil)
            (let [filtered-image (.filter (RescaleOp. scales offsets nil) img disp)] ;b-o (currently overriding offset)
              (draw-image disp gr x y)))))


(defn draw-image-brightness
  "gr, brightness value, return brightness draw fn"
  [img gr b b-o x y]
    (let [disp (BufferedImage. (.getWidth img nil) (.getHeight img nil) BufferedImage/TYPE_INT_ARGB)]
          (do
            (.drawImage (.getGraphics disp)  img 0 0 nil)
            (let [filtered-image (.filter (RescaleOp. (float b) 10.0 nil) img disp)] ;b-o (currently overriding offset)
              (draw-image disp gr x y)))))
