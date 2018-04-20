(ns upstream.entities.entitymanager
  (:require
    [upstream.config :as config]
    [upstream.utilities.images :as images]
    [upstream.tilemap.tilemanager :as tile-manager]
    [upstream.utilities.spacial :as spacialutility])
  (:gen-class))

(defn load-entities
  "perform resource loads on list of entities, TODO: create draw-handler for each"
  [entity-list]
  ;TODO: load decisions if added
  (let [load-image #(images/load-image-scale-by-factor % @config/COMPUTED-SCALE)]
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
            :map-resource (tile-manager/load-map-resource config/LEVEL-ONE-TILEMAPS starting-x starting-y))))
          entity-list)))

(defn get-central-render-map
  "take entity list and return the map of the player"
  [entity-list]
  (reduce #(if (:render-as-central %2) (reduced (:map-resource %2)) %1) false entity-list))

(defn update-entities
  "update given entity (either from decisions or player input)"
  [entities update-map] ;NOTE: update-map may be empty
  (let [update-facing (:update-facing update-map)
        update-action (:update-action update-map)]
    (map (fn [e]
            (let [facing (:facing e)
                  action (:current-action e)
                  map-resource (:map-resource e)
                  px (:position-x e)
                  py (:position-y e)]
            (if (:render-as-central e)
                (assoc e :map-resource (tile-manager/set-position px py map-resource))
                (assoc e :map-resource (tile-manager/update-chunk-view px py map-resource))))) entities)
  ))

(defn draw-entity
  "draw given entity (should be used as draw handler in tilemap ns)"
  [gr e x y]
  (let [action-set ((:current-action e) (:images e))
        current-image (nth ((:facing e) action-set)
                           (:current-frame-index action-set))]
    (images/draw-image
      current-image gr
      (- x (* @config/COMPUTED-SCALE (:draw-width-offset e)))
      ;TODO: figure out correct image offsets given positioning
      (- y (* @config/COMPUTED-SCALE (:draw-height-offset e))))))

(defn create-draw-handlers
  "take all entities in list and create a list of draw handlers"
  [entities grid-dim]
  (map #(let [central-chunk (:central-chunk (first (:current-maps (:map-resource %))))
              chunk-dim (:chunk-dim (:map-resource %))
              offset-x (* grid-dim (- (:offset-x central-chunk) chunk-dim))
              offset-y (* grid-dim (- (:offset-y central-chunk) chunk-dim))
              chunk-relative-x (- (:position-x %) offset-x)
              chunk-relative-y (- (:position-y %) offset-y)]
              (hash-map :x (int (/ chunk-relative-x grid-dim))
                        :y (int (/ chunk-relative-y grid-dim))
                        ;TODO: add prevent-block? for player
                        :fn (fn [gr map-offset-x map-offset-y]
                                (let [iso-coords (spacialutility/cartesian-to-isometric-transform
                                                      (list (+ chunk-relative-x map-offset-x)
                                                            (+ chunk-relative-y map-offset-y)))]
                                    (draw-entity gr % (first iso-coords) (second iso-coords))))))
  entities))

(defn entitykeypressed
  "respond to key press"
  [key current-control-map]
  ;return entity update map i.e.
  ;calculate running vs. walking with shift
  (let [update-direction :south ;TODO
        update-speed (cond (and
                            (= (:update-action current-control-map) :walking)
                            (= key :shift)) :running
                           ; (and
                           ;  (= (:move-type current-control-map) :at-rest)
                           ;  (= key )
                           ;   )
                             )]
  {:update-facing update-direction
   :update-action update-speed}))

(defn entitykeyreleased
  "respond to key release"
  [key current-control-map]
  ;return entity update map
  {:update-facing :speed
   :update-action :at-rest})
