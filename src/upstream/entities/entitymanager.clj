(ns upstream.entities.entitymanager
  (:require
    [upstream.config :as config]
    [upstream.utilities.images :as images]
    [upstream.tilemap.tilemanager :as tile-manager]
    [upstream.tilemap.tileinterface :as tile-interface]
    [upstream.entities.entitydecisionmanager :as decisions]
    [upstream.utilities.spacial :as spacialutility]
    [upstream.utilities.lighting :as lighting])
  (:gen-class))

(defmacro defmove
  "macro for defining a move-transform"
  [a] (list 'fn '[x y s]
          (list 'spacialutility/pt-at-angle 'x 'y
              (list 'Math/toRadians a) 's)))

(def update-xy
    {:north (defmove 270)
     :north-east (defmove 315)
     :east (defmove 0)
     :south-east (defmove 45)
     :south (defmove 90)
     :south-west (defmove 135)
     :west (defmove 180)
     :north-west (defmove 225)})

(defn get-speed
  "get animation movement vector from action"
  [action]
  (cond (= action :walking) config/WALKING-SPEED
        (= action :running) config/RUNNING-SPEED
        (= action :walk-jumping) config/WALKING-SPEED
        (= action :run-jumping) config/RUNNING-SPEED
        :else 0))

(def jumping? (fn [action]
  (or (= action :walk-jumping)
      (= action :run-jumping)
      (= action :static-jumping))))

(defn load-entities
  "perform resource loads on list of entities, TODO: create draw-handler for each"
  [entity-list]
  (let [load-image #(images/load-image %)]
    ;preload map resources
    (tile-manager/preload-map-resources! config/LEVEL-ONE-TILEMAPS)
    (map (fn [entity]
          (let [starting-x (:position-x entity)
                starting-y (:position-y entity)
                role (:render-as-central entity)]
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
            :map-resource (tile-manager/load-map-resource config/LEVEL-ONE-TILEMAPS starting-x starting-y role)
            :decisions (decisions/load-entity-decisions (:decisions entity)))))
          entity-list)))

(defn get-central-render-map
  "take entity list and return the map of the player"
  [entity-list]
  (reduce #(if (:render-as-central %2) (reduced (:map-resource %2)) %1) false entity-list))

(defn update-entities
  "update given entity (either from decisions or player input) (works with both online and local)"
  [entities make-graphical-adjustment?]
  (let [all-positions (map #(list (:position-x %) (:position-y %)) entities)]
    (map (fn [e]
            (let [map-resource (:map-resource e)
                  px (:position-x e)
                  py (:position-y e)
                  pz (:position-z e) ;current height
                  p-dz (:height-dz e) ;current change-in-height
                  occupied-tile-height (tile-interface/get-tile-height
                                          (:map-resource e)
                                          (list (:position-x e) (:position-y e)))
                  update-source (if (= (:control-input e) :decisions)
                                    (decisions/make-player-decision
                                        (merge e {:all-positions all-positions}))
                                    (:control-input e))
                  updated-facing (:update-facing update-source)
                  updated-action (:update-action update-source)
                  updated-position (tile-interface/try-move
                                              (updated-facing update-xy) px py
                                              (:collision-diameter e)
                                              (get-speed updated-action)
                                              map-resource)
                  updated-dz (if (and (jumping? updated-action) (= p-dz 0))
                                 config/JUMP-MAGNITUDE
                                (- p-dz config/GRAVITY-PER-FRAME))
                  updated-x (first updated-position)
                  updated-y (second updated-position)
                  updated-z (max occupied-tile-height (+ pz updated-dz))
                  updated-map (if make-graphical-adjustment?
                                  (tile-manager/set-position updated-x updated-y map-resource)
                                  (tile-manager/update-chunk-view updated-x updated-y map-resource))]
                  (merge e (hash-map :map-resource updated-map
                                     :facing updated-facing
                                     :position-x updated-x
                                     :position-y updated-y
                                     :position-z updated-z
                                     :height-dz updated-dz
                                     :current-action updated-action))))
           entities)))

(defn draw-entity
  "draw given entity (should be used as draw handler in tilemap ns)"
  [gr e x y]
  (let [action-set ((:current-action e) (:images e))
        current-image (nth ((:facing e) action-set)
                           (:current-frame-index action-set))
        x-correct (- x (:draw-width-offset e))
        y-correct (- y (:draw-height-offset e))
        current-height (:position-z e)]
    (do
      (lighting/cast-shadow gr x y (* 2 (:collision-diameter e))) ;TODO center shadow
      (images/draw-image current-image gr x-correct (+ y-correct current-height)))))

(defn create-draw-handlers
  "take all entities in list and create a list of draw handlers"
  [entities]
  (map #(let [corner-chunk (:corner-chunk (first (:current-maps (:map-resource %))))
              grid-dim (:grid-dim (:map-resource %))
              chunk-relative-pt (spacialutility/map-relative-to-chunk-relative
                                    (:position-x %) (:position-y %)
                                    (:offset-x corner-chunk) (:offset-y corner-chunk)
                                    grid-dim)
              tile-location-pt (spacialutility/pt-to-grid chunk-relative-pt grid-dim)]
              (hash-map :x (first tile-location-pt)
                        :y (second tile-location-pt)
                        :prevent-block? (:render-as-central %)
                        :fn (fn [gr map-offset-x map-offset-y]
                                (let [iso-coords (spacialutility/cartesian-to-isometric-transform
                                                      (list (+ (first chunk-relative-pt) map-offset-x)
                                                            (+ (second chunk-relative-pt) map-offset-y)))]
                                    (draw-entity gr %
                                            (int (first iso-coords))
                                            (int (second iso-coords)))))))
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
                              (= (:update-action current-control-map) :walking)
                              (= key :space)) :walk-jumping
                           (and
                              (= (:update-action current-control-map) :at-rest)
                              (= key :space)) :static-jumping
                           (and
                              (= (:update-action current-control-map) :running)
                              (= key :space)) :run-jumping
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
                              (= (:update-action current-control-map) :walk-jumping)
                              (= key :space)) :walking
                           (and
                              (= (:update-action current-control-map) :run-jumping)
                              (= key :space)) :running
                           (and
                              (= (:update-action current-control-map) :static-jumping)
                              (= key :space)) :at-rest
                           (and
                              (or
                                (= (:update-action current-control-map) :walking)
                                (= (:update-action current-control-map) :running))
                              (directional? key)) :at-rest
                            :else (:update-action current-control-map))]
  (hash-map :update-facing update-direction
            :update-action update-speed)))
