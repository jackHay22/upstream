(ns upstream.entities.entitymanager
  (:require
    [upstream.config :as config]
    [upstream.utilities.images :as images]
    [upstream.tilemap.tilemanager :as tile-manager]
    [upstream.tilemap.tileinterface :as tile-interface]
    [upstream.entities.entitydecisionmanager :as decisions]
    [upstream.utilities.spacial :as spacialutility])
  (:gen-class))

(def update-xy
  (let [at-angle #(spacialutility/pt-at-angle %1 %2 (Math/toRadians %4) %3)]
       {:north (fn [x y s] (list x (- y s)))
        :north-east (fn [x y s] (at-angle x y s 45))
        :east (fn [x y s] (list (+ x s) y))
        :south-east (fn [x y s] (at-angle x y s 315))
        :south (fn [x y s] (list x (+ y s)))
        :south-west (fn [x y s] (at-angle x y s 225))
        :west (fn [x y s] (list (- x s) y))
        :north-west (fn [x y s] (at-angle x y s 135))}))

(defn get-speed
  "get animation movement vector from action"
  [action]
  (cond (= action :walking) config/WALKING-SPEED
        (= action :running) config/RUNNING-SPEED
        :else 0))

(defn load-entities
  "perform resource loads on list of entities, TODO: create draw-handler for each"
  [entity-list]
  ;TODO: load decisions if added
  (let [load-image #(images/load-image %)]
    (map (fn [entity]
          (let [starting-x (:position-x entity)
                starting-y (:position-y entity)]
          (assoc (update-in entity [:images]
              (fn [state-map]
                  (reduce (fn [all-states current-state]
                            (update-in all-states [current-state]
                                   (fn [directions-map]
                                      (reduce (fn [all-directions current-direction]
                                                  (update-in all-directions [current-direction]
                                                        #(doall (if (not (empty? %))
                                                                    (map load-image %)))))
                                              directions-map (:all-directions entity)))))
                        state-map (:all-states entity))))
            :map-resource (tile-manager/load-map-resource config/LEVEL-ONE-TILEMAPS starting-x starting-y)
            :decisions (decisions/load-entity-decisions (:decisions entity)))))
          entity-list)))

(defn get-central-render-map
  "take entity list and return the map of the player"
  [entity-list]
  (reduce #(if (:render-as-central %2) (reduced (:map-resource %2)) %1) false entity-list))

(defn update-entities
  "update given entity (either from decisions or player input)"
  [entities update-map]
  (let [all-positions (map #(list (:position-x %) (:position-y %)) entities)]
    (map (fn [e]
            (let [map-resource (:map-resource e)
                  px (:position-x e)
                  py (:position-y e)
                  ;TODO: support for list of entity control maps and merging with entity list
                  ;(or empty input)
                  update-source (if (:render-as-central e)
                                    update-map
                                    (decisions/make-player-decision
                                        (merge e {:all-positions all-positions})))
                  updated-facing (:update-facing update-source)
                  updated-action (:update-action update-source)
                  updated-position (tile-interface/try-move
                                              (updated-facing update-xy) px py (get-speed updated-action)
                                              map-resource)
                  updated-x (first updated-position)
                  updated-y (second updated-position)
                  updated-map (if (:render-as-central e)
                                  (tile-manager/set-position updated-x updated-y map-resource)
                                  (tile-manager/update-chunk-view px py map-resource))]
                  (merge e (hash-map :map-resource updated-map
                                     :facing updated-facing
                                     :position-x updated-x
                                     :position-y updated-y
                                     :current-action updated-action))))
           entities)))

(defn associate-update-maps
  "add update maps to entities"
  [entities])

(defn draw-entity
  "draw given entity (should be used as draw handler in tilemap ns)"
  [gr e x y]
  (let [action-set ((:current-action e) (:images e))
        current-image (nth ((:facing e) action-set)
                           (:current-frame-index action-set))]
    (images/draw-image current-image gr x y)))

(defn create-draw-handlers
  "take all entities in list and create a list of draw handlers"
  [entities]
  (map #(let [corner-chunk (:corner-chunk (first (:current-maps (:map-resource %))))
              chunk-dim (:chunk-dim (:map-resource %))
              grid-dim (:grid-dim (:map-resource %))
              offset-x (* grid-dim (:offset-x corner-chunk))
              offset-y (* grid-dim (:offset-y corner-chunk))
              chunk-relative-x (- (:position-x %) offset-x)
              chunk-relative-y (- (:position-y %) offset-y)]
              (hash-map :x (int (/ chunk-relative-x grid-dim))
                        :y (int (/ chunk-relative-y grid-dim))
                        :prevent-block? (:render-as-central %)
                        :fn (fn [gr map-offset-x map-offset-y]
                                (let [iso-coords (spacialutility/cartesian-to-isometric-transform
                                                      (list (+ chunk-relative-x map-offset-x)
                                                            (+ chunk-relative-y map-offset-y)))
                                      iso-x (- (int (first iso-coords)) (:draw-width-offset %))
                                      iso-y (- (int (second iso-coords)) (:draw-height-offset %))]
                                    (draw-entity gr % iso-x iso-y)))))
  entities))

(defn entitykeypressed
  "respond to key press"
  [key current-control-map]
  (let [directional? #(or (= % :up) (= % :down) (= % :left) (= % :right))
        update-direction (cond
                            (= key :down) :south
                            (= key :left) :west
                            (= key :right) :east
                            (= key :up) :north
                            :else (:update-facing current-control-map))
        update-speed (cond (and
                              (= (:update-action current-control-map) :walking)
                              (= key :shift)) :running
                           (and
                              (= (:update-action current-control-map) :at-rest)
                              (directional? key)) :walking
                            :else (:update-action current-control-map))]
  (hash-map :update-facing update-direction
            :update-action update-speed)))

(defn entitykeyreleased
  "respond to key release"
  [key current-control-map]
  (let [directional? #(or (= % :up) (= % :down) (= % :left) (= % :right))
        update-direction (:update-facing current-control-map)
        ;TODO: make sure both sprint and directional key releases registered
        update-speed (cond (and
                              (= (:update-action current-control-map) :running)
                              (= key :shift)) :walking
                           (and
                              (or
                                (= (:update-action current-control-map) :walking)
                                (= (:update-action current-control-map) :running))
                              (directional? key)) :at-rest
                            :else (:update-action current-control-map))]
  (hash-map :update-facing update-direction
            :update-action update-speed)))
