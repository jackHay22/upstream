(ns upstream.utilities.spacial
  (:require [upstream.config :as config])
  (:gen-class))

(defn lateral-range
  "create a lateral range for drawing tiles at depth rather than by row"
  [grid-dim]
  (let [make-range-layer #(map vector
                  (take (+ % 1) (range)) (take (+ % 1) (range % -1 -1)))]
  (mapcat (fn [row remove] (take (- grid-dim remove) (drop remove (make-range-layer row))))
          (range (- (* grid-dim 2) 1))
          (concat (repeat (- grid-dim 1) 0) (range grid-dim)))))

(defn cartesian-to-isometric-transform
  "take cartesian (x,y) and map to isometric (x,y)"
  [xy]
  (list (- (first xy) (second xy))
        (/ (+ (first xy) (second xy)) 2)))

(defn isometric-to-cartesian-transform
  "take isometric (x,y) and map to cartesian (x,y)"
  [xy]
  (list (+ (second xy) (/ (first xy) 2))
        (- (second xy) (/ (first xy) 2))))

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
