(ns upstream.tilemap.tiles
  (:require
    [upstream.config :as config]
    [clojure.java.io :as io]
    [upstream.utilities.images :as images])
  (:gen-class))

(defn parse-map-file
  "resource path, list of keywords for storing the game map as a list of maps (i.e. '(:image :sound)
  or '(:image :sound :height :blocked?))"
  [path fields]
  (with-open [reader (clojure.java.io/reader (io/resource path))]
    (map (fn [line]
        (map (fn [sub-line]
                  (let [location-set
                          (zipmap fields (map #(Integer. %)
                            (clojure.string/split sub-line #",")))]
                  (assoc location-set :draw? (not (= -1 ((first fields) location-set))))))
        (clojure.string/split line #" ")))
      (clojure.string/split-lines (clojure.string/join "\n" (line-seq reader))))))

(defn get-tile
  "given (loaded map), get x,y tile: {:image _ ...}"
  [loaded-map x y])

(defn set-position
    "set tile-map position: based off player's location in map"
    [px py tilemap]
    (let [increment-width (:increment-width tilemap)
          increment-height (/ increment-width 2)
          window-width @config/WINDOW-WIDTH
          window-height @config/WINDOW-HEIGHT
          fix-offset-at-edges (fn [computed-negative-offset max-negative-offset buffer]
                                (cond
                                    (> computed-negative-offset buffer) buffer
                                    (> max-negative-offset
                                       computed-negative-offset) max-negative-offset
                                    :else computed-negative-offset))
          initial-window-offset-x (- (/ window-width 2) px)
          initial-window-offset-y (- (/ window-height 2) py (:increment-width tilemap))
          tilemap-negative-offset-x (fix-offset-at-edges initial-window-offset-x
                                      (+ (- window-width
                                            (* increment-width (:tiles-across tilemap)))
                                         increment-width)
                                      (- 0 increment-width))
          tilemap-negative-offset-y (fix-offset-at-edges initial-window-offset-y
                                      (+ (- window-height
                                            (* increment-height (:tiles-down tilemap)))
                                         increment-height) ;TODO: change to account for layer offset
                                      (- 0 increment-height))]
          (merge tilemap
            {:map-offset-x tilemap-negative-offset-x
             :map-offset-y tilemap-negative-offset-y
             :start-display-x (- (int (/ (- tilemap-negative-offset-x) increment-width)) 1)
             :start-display-y (- (int (/ (- tilemap-negative-offset-y) increment-height)) 1)})))

(defn split-master
  "split master image into list of image maps: {:image :width :height} (1 dimensional)"
  [block-loader tile-width tile-height]
  (let [x-coords (range 0 (:resource-width block-loader) tile-width)
        y-coords (range 0 (:resource-height block-loader) tile-height)]
  (flatten
      (map (fn [r]
           (map (fn [c]
              {:image ((:load-fn block-loader) c r tile-width tile-height)
               :width tile-width
               :height tile-height}) x-coords)) y-coords))))

(defn init-tile-map
  "take tilemap resource and master tilemap document"
  [tilemap-set]
    ;TODO: correct loading with mapcat
  (let [loaded-map (parse-map-file
                      (:map-path tilemap-set)
                      (:loaded-map-fields tilemap-set))
        increment-width (* @config/COMPUTED-SCALE (:spacing-paradigm tilemap-set))
        draw-peripheral-superblocks? (if (and
                                          (not @config/HEADLESS-SERVER?)
                                          (= (:render-optimization tilemap-set) config/RENDER-OVERSIZED)) true false)
        factor-reduce (fn [type]
                          (fn [largest next]
                              (if (> (type next) largest) (type next) largest)))
        loaded-images (if (not @config/HEADLESS-SERVER?)
                              (mapcat (fn [block]
                                      (let [block-loader (images/sub-image-loader (:img block))]
                                            ;transform resource block attribute by scaling image
                                            (map #(merge %
                                                          {:image (images/scale-loaded-image-by-factor (:image %) @config/COMPUTED-SCALE)
                                                           :width (* @config/COMPUTED-SCALE (:width %))
                                                           :height (* @config/COMPUTED-SCALE (:height %))})
                                                  (split-master block-loader
                                                    (:tile-width block) (:tile-height block)))))
                                      (:tiles-data tilemap-set)))]
  ;return transformed resource
  {:loaded-images loaded-images
   :increment-width increment-width
   :map-offset-x 0
   :map-offset-y 0
   :start-display-x 0
   :start-display-y 0
   :draw-peripheral-superblocks? draw-peripheral-superblocks?
   :largest-superblock-width (if draw-peripheral-superblocks?
                                 (int (/ (reduce (factor-reduce :width)
                                         (:width (first loaded-images)) loaded-images) increment-width)) 0)
   :largest-superblock-height (if draw-peripheral-superblocks?
                                  (int (/ (reduce (factor-reduce :height)
                                          (:height (first loaded-images)) loaded-images) (/ increment-width 2))) 0)
   :tiles-down (count loaded-map)
   :tiles-across (count (first loaded-map))
   :display-across (+ config/TILES-ACROSS 2) ;TODO: different with a different spacing type
   ;TODO: improve
   :display-down (+ 2 (/ @config/WINDOW-HEIGHT (/ (* @config/COMPUTED-SCALE (:spacing-paradigm tilemap-set)) 4)))
   :map loaded-map}))

(defn entity-handler
  "execute handlers at correct y value"
  [handlers]
  (fn [y]
    ;TODO: not working
    (doall (map #(if (= y (:y %)) ((:fn %))) handlers))))

(defn get-tile
  [px py tilemap]

  )

(defn render-map
  "render a tilemap/set in loaded form (as tilemap is rendered, system
    will render game entities by providing an x value to any subscribing
    systems)"
  [gr tilemap]
  (let [increment-width (:increment-width tilemap)
        handle-at-y (entity-handler (:entity-handlers tilemap))
        start-draw-x (:start-display-x tilemap)
        start-draw-y (:start-display-y tilemap)
        check-drawable-blocks? (:draw-peripheral-superblocks? tilemap)
        offset-fn (fn [x y] ;handle alternating offset
                      (+ (* x increment-width)
                         (if (even? y)
                             (/ increment-width 2) 0)))
        range-across (if check-drawable-blocks?
                         (range
                           (- start-draw-x (:largest-superblock-width tilemap))
                           (+ start-draw-x (:display-across tilemap) (:largest-superblock-width tilemap)))
                         (range start-draw-x (+ start-draw-x (:display-across tilemap))))
        range-down (if check-drawable-blocks?
                       (range
                          (- start-draw-y (:largest-superblock-height tilemap))
                          (+ start-draw-y (:display-down tilemap) (:largest-superblock-height tilemap)))
                       (range start-draw-y (+ start-draw-y (:display-down tilemap))))]

        (doall (map (fn [y]
            (do (handle-at-y y)
            (doall (map (fn [x]
                (if (and
                        (and (>= x 0) (> (:tiles-across tilemap) x))
                        (and (>= y 0) (> (:tiles-down tilemap) y)))

                        (let [map-entry (nth (nth (:map tilemap) y) x)
                              r-loc (int (+ (* y (/ increment-width 4)) (:map-offset-y tilemap)))
                              c-loc (int (+ (offset-fn x y) (:map-offset-x tilemap)))]
                            (do
                              (if (:draw? map-entry)
                                  ;(if check-drawable-blocks?
                                      ;() ;TODO: future optimization by way of only drawing objects that actually appear
                                      (images/draw-image
                                        (:image (nth (:loaded-images tilemap) (:image-index map-entry)))
                                        gr c-loc r-loc))))))
                range-across))))
                range-down))))
