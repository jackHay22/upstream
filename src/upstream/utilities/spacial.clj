(ns upstream.utilities.spacial
  (:require [upstream.config :as config])
  (:gen-class))

(defn get-entity-tile
  "given an entities x,y, return tile index pair"
  [px py]
  (let [tile-width (* @config/COMPUTED-SCALE config/ORIGINAL-TILE-WIDTH)
        tile-height (* @config/COMPUTED-SCALE config/ORIGINAL-TILE-HEIGHT)
        ]


    ))

(defn cartesian-to-isometric-transform
  "take cartesian x,y and map to isometric (x,y)"
  [xy]
  (list (- (first xy) (second xy))
        (/ (+ (first xy) (second xy)) 2)))

(defn isometric-to-cartesian-transform
  "take isometric x,y and map to cartesian (x,y)"
  [xy]
  (list (+ (second xy) (/ (first xy) 2))
        (- (second xy) (/ (first xy) 2))))

(defn coords-equal?
  "check if two x,y pairs are equal"
  [xy1 xy2]
  (and (= (first xy1) (first xy2))
       (= (second xy1) (second xy2))))
