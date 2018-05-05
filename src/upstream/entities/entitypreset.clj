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
    :control-input {:update-facing :south :update-action :at-rest} ;or :decisions
    :render-as-central true
    :decisions nil ;decisions listing
    :performance {}
    :map-resource nil
    :position-x starting-x
    :position-y starting-y
    :draw-height-offset 120
    :draw-width-offset 3
    :collision-diameter 30
    :facing :south-west ;TODO remove?
    :current-action :at-rest
    :run-stamina 300 })

(def ai-preset-1
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
    :control-input :decisions
    :render-as-central false
    :decisions nil ;decisions listing (TODO)
    :performance {}
    :map-resource nil
    :position-x 700
    :position-y 700
    :draw-height-offset 120
    :draw-width-offset 3
    :collision-diameter 30
    :facing :south-west ;TODO remove?
    :current-action :at-rest
    :run-stamina 300 })
