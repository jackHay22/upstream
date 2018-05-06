(ns upstream.utilities.spacial
  (:require [upstream.config :as config])
  (:gen-class))

(defn lateral-range
  "create a lateral range for drawing tiles at depth rather than by row"
  [grid-dim]
  (let [make-range-layer #(map vector
                  (take (+ %1 1) (range)) (take (+ %1 1) (range %1 -1 -1)) (repeat %2))]
  (mapcat (fn [row remove b-factor] (take (- grid-dim remove) (drop remove (make-range-layer row b-factor))))
          (range (- (* grid-dim 2) 1))
          (concat (repeat (- grid-dim 1) 0) (range grid-dim))
          (range 0.8 1.8 (/ 0.5 grid-dim)))))

(defn pt-to-grid
  "take chunk-relative pt and return grid coords"
  [pt grid-dim]
  (list
    (int (Math/floor (/ (first pt) grid-dim)))
    (int (Math/floor (/ (second pt) grid-dim)))))

(defn map-relative-to-chunk-relative
  "take pt, offsets, grid dim, determine chunk-relative"
  [x y chunk-offset-x chunk-offset-y grid-dim]
  (list
    (- x (* chunk-offset-x grid-dim))
    (- y (* chunk-offset-y grid-dim))))

(defn grid-pt-in-chunk?
  "take pt and determine if in current chunk"
  [pt chunk-offset-x chunk-offset-y chunk-dim grid-dim]
  (and
    ;TODO: verify
    (> (first pt) (* chunk-offset-x grid-dim))
    (> (* (+ chunk-offset-x chunk-dim) grid-dim) (first pt))
    (> (second pt) (* chunk-offset-y grid-dim))
    (> (* (+ chunk-offset-y chunk-dim) grid-dim) (second pt))))

(defn cartesian-to-isometric-transform-clockwise
  "take cartesian (x,y) and map to isometric (x,y)"
  [xy]
  (list (- (first xy) (second xy))
        (/ (+ (first xy) (second xy)) 2)))

(defn cartesian-to-isometric-transform
  "take cartesian (x,y) and map to isometric (x,y)
  but with counterclockwise rotation"
  [xy]
  (list (- (first xy) (second xy))
        (/ (+ (- 0 (first xy)) (second xy)) 2)))

(defn isometric-to-cartesian-transform
  "take isometric (x,y) and map to cartesian (x,y)"
  [xy]
  (list (+ (second xy) (/ (first xy) 2))
        (- (second xy) (/ (first xy) 2))))

(defn get-isometric-bounds
  "take cartesian x,y dim of box, and return 4 iso pts"
  [xy bounding-dim]
  (let [origin-x (first xy)
        origin-y (second xy)
        ordered-x (take 4 (cycle (list origin-x bounding-dim)))
        ordered-y (concat (repeat 2 origin-y) (repeat 2 bounding-dim))]
  (map cartesian-to-isometric-transform
    (map vector ordered-x ordered-y))))

(defn coords-equal?
  "check if two x,y pairs are equal"
  [xy1 xy2]
  (and (= (first xy1) (first xy2))
       (= (second xy1) (second xy2))))

(defn pt-at-angle
  "given angle dist and pt, generate resultant pt"
  [x y a dist]
  (list
      (+ x (* dist (Math/cos a)))
      (+ y (* dist (Math/sin a)))))

(defn function-from-pts
  "generate line function from pts"
  [xy1 xy2]
  (let [slope (/ (- (second xy2) (second xy1)) (- (first xy2) (first xy1)))]
      (fn [x] (+ (* (- x (first xy1)) slope) (second xy1)))))
