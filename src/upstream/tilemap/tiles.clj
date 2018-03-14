(ns upstream.tilemap.tiles
  (:require
    [seesaw.graphics :as sawgr]
    [upstream.engine.config :as params]
    [seesaw.icon :as sawicon])
  (:gen-class))

(import javax.imageio.ImageIO)

;render frame
;TODO: more with overlap etc...
;TODO: load these in init function for updated width
(def display-tiles-across (+ (int (/ @params/WINDOW-WIDTH params/TILE-WIDTH)) 2))
(def display-tiles-down (+ (int (/ @params/WINDOW-HEIGHT params/TILE-HEIGHT)) 2))

(def fix-bounds
  "take values and protect against overrun and underrun"
  (fn [dim min]         ;dimension
    (cond (> dim 0) 0
          (< dim min) min
          :else dim)))  ;usually width/height

;TODO
; (defn set-position
;     "set tile-map position: based off player loc"
;     [x y level]
;     (let [bounds-x (fix-bounds x
;                       (- (/ params/WINDOW-WIDTH params/scale)
;                          (* (:tiles-across (level allmaps)) params/TILE-WIDTH)))
;           bounds-y (fix-bounds y
;                       (- (/ params/WINDOW-HEIGHT params/scale)
;                          (* (:tiles-down (level allmaps)) params/TILE-WIDTH)))]
;     (reset! position-x bounds-x)
;     (reset! position-y bounds-y)
;     (reset! start-display-x (int (/ (- bounds-x) params/tile-width)))
;     (reset! start-display-y (int (/ (- bounds-y) params/tile-width)))))

; (defn load-tilemap
;   "Load tilemap from file into a 2D list of image numbers"
;   [maploc]
;   (map (fn [line] (map #(Integer. %)  (clojure.string/split line #" ")))
;     (clojure.string/split-lines (slurp maploc))))
;
; (defn get-tile
;   "take x and y coordinate, return tile type (i.e walkable, visual barrier, water)"
;   [x y map layer]
;   (let [type (nth (nth (layer (map allmaps)) y) x)
;         result-map {:water? false :walkable? false :visual-barrier? false}
;         ;perform various checks, pass map through each
;         check-solid (if (not= type 0) (assoc result-map :solid? true) result-map)
;         check-dark (if (contains? (:dark (map allmaps)) '(x y)) (assoc check-solid :dark? true) check-solid)]
;     (if (contains? (:terminals (map allmaps)) '(x y)) (assoc check-dark :terminal? true) check-dark)))
;
; (defn draw-image
;   "take graphics object, map that contains map objects and image lists,
;   and layer number to retrieve, draws image at coordinate (lambda input)"
;   [gr map-and-images layer]
;   (fn [x y]
;     (let [image-number (nth (nth (layer map-and-images) y) x)
;           r-loc (+ (* y params/tile-width) (deref position-y))
;           c-loc (+ (* x params/tile-width) (deref position-x))]
;       (if (not= image-number 0)
;       (sawgr/draw gr
;         (sawgr/image-shape (int c-loc) (int r-loc)
;           (nth (:images map-and-images) image-number))
;           (sawgr/style))) x)))
;
; (defn draw-current-tiles
;   "draw current tiles in view"
;   [tile-map gr layer]
;   (let [x-coords (range (deref start-display-x)  (+ display-tiles-across (deref start-display-x)))
;         y-coords (range (deref start-display-y)  (+ display-tiles-down (deref start-display-y)))
;
;         ;maximum map values
;         map-x (:tiles-across (tile-map allmaps))
;         map-y (:tiles-down (tile-map allmaps))
;
;         ;draw for current map/layer
;         tile-drawer (draw-image gr (tile-map allmaps) layer)]
;
;         ;doseq for side effects
;         (doseq [x x-coords y y-coords]
;             (if (and (>= x 0) (>= y 0) (< x map-x) (< y map-y))
;                 (tile-drawer x y)))))
