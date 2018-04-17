(ns upstream.entities.entitypreset
  (:gen-class))

(defrecord StateImageCollection [current-frame-index
             north north-east east south-east
             south south-west west north-west])

(defn player-preset-1
  "take x,y and return preset"
  [starting-x starting-y]
  ;Sample Individual Entity State:
  ;each state has a list of cycling animation frames for each of the 8 directions
  {:images {
         :display "entities/logger_1.png" ;TODO: load as sheets rather than image lists
         :at-rest (StateImageCollection. 0 '("entities/idle_rough_n.png") '()
                                           '() '("entities/idle_rough_se.png")
                                           '("entities/idle_rough_s.png") '("entities/idle_rough_sw.png")
                                           '() '())
         :walking (StateImageCollection. 0 '() '() '() '() '() '() '() '())
         :running (StateImageCollection. 0 '() '() '() '() '() '() '() '())
         :punching (StateImageCollection. 0 '() '() '() '() '() '() '() '())
    }
    :all-states (list :at-rest :walking :running :punching)
    :all-directions (list :north :north-east
                          :east :south-east
                          :south :south-west
                          :west :north-west)
    :logical-entity-id 0
    :decisions false
    :map-resource nil
    :render-as-central false
    :position-x starting-x
    :position-y starting-y
    :draw-height-offset 20
    :draw-width-offset 20
    :facing :south-west
    :current-action :at-rest
    :run-stamina 300 })
