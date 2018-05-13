(ns upstream.utilities.spacial
  (:require [upstream.config :as config])
  (:gen-class))

(defn lateral-range-clockwise
  "create a lateral range for drawing tiles at depth rather than by row"
  [grid-dim]
  (let [make-range-layer #(map vector
                  (take (+ %1 1) (range)) (take (+ %1 1) (range %1 -1 -1)))]
  (mapcat (fn [row remove] (take (- grid-dim remove) (drop remove (make-range-layer row))))
          (range (- (* grid-dim 2) 1))
          (concat (repeat (- grid-dim 1) 0) (range grid-dim)))))

(defn lateral-range-counterclockwise
  "create a lateral range corresponding
  to depth sequence of counterclockwise linear transformation"
  [grid-dim]
  (let [starting-x-range (rseq (vec (range (- grid-dim) grid-dim)))
        retain (concat (range 1 grid-dim) (repeat grid-dim))
        remove (concat (repeat (- grid-dim 1) 0) (range))]
        (mapcat (fn [x-begin retain trim]
                    (drop trim
                      (take retain
                        (iterate #(map inc %1) (vector x-begin 0)))))
                starting-x-range retain remove)))

(def lateral-range-cached (memoize lateral-range-counterclockwise))

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

(defn cartesian-to-isometric-transform-counter-clockwise
  "take cartesian (x,y) and map to isometric (x,y)
  but with counterclockwise rotation"
  [xy]
  (list (+ (first xy) (second xy))
        (/ (+ (- (first xy)) (second xy)) 2)))

(defn cartesian-to-isometric-transform
  "wrapper for toggling between transforms"
  [xy]
  (cartesian-to-isometric-transform-counter-clockwise xy))

(defn isometric-to-cartesian-transform-clockwise
  "take isometric (x,y) and map to cartesian (x,y)"
  [xy]
  (list (+ (second xy) (/ (first xy) 2))
        (- (second xy) (/ (first xy) 2))))

(defn isometric-to-cartesian-transform-counter-clockwise
  "take isometric (x,y) and map to cartesian (x,y)"
  [xy]
  (list (+ (- (second xy)) (/ (first xy) 2))
        (+ (second xy) (/ (first xy) 2))))

(defn isometric-to-cartesian-transform
  "transform wrapper"
  [xy]
  (isometric-to-cartesian-transform-counter-clockwise xy))

(defn get-bounds
  "take cartesian x,y dim of box, and return 4 iso pts"
  [xy b-w b-h]
  (map vector
    (take 4 (cycle (list (first xy) (+ (first xy) b-w))))
    (concat (repeat 2 (second xy)) (repeat 2 (+ (second xy) b-h)))))

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
