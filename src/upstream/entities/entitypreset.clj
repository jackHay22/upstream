(ns upstream.entities.entitypreset
  (:gen-class))

(defn player-preset-1
  "take x,y and return preset"
  [starting-x starting-y]
  ;Sample Individual Entity State:
  ;each state has a list of cycling animation frames for each of the 8 directions
  {:images {
          :display "entities/logger_1.png"
          :at-rest {
            :current-frame-index 0
            :north '("entities/idle_rough_n.png")
            :north-east '()
            :east '()
            :south-east '("entities/idle_rough_se.png")
            :south '("entities/idle_rough_s.png")
            :south-west '("entities/idle_rough_sw.png")
            :west '()
            :north-west '()
          }
         :walking {
           :current-frame-index 0
           :north '()
           :north-east '()
           :east '()
           :south-east '()
           :south '()
           :south-west '()
           :west '()
           :north-west '()
         }
         :running {
           :current-frame-index 0
           :north '()
           :north-east '()
           :east '()
           :south-east '()
           :south '()
           :south-west '()
           :west '()
           :north-west '()
         }
         :punching {
           :current-frame-index 0
           :north '()
           :north-east '()
           :east '()
           :south-east '()
           :south '()
           :south-west '()
           :west '()
           :north-west '()
         }
        }
    :all-states (list :at-rest :walking :running :punching)
    :all-directions (list :north :north-east
                          :east :south-east
                          :south :south-west
                          :west :north-west)
    :logical-entity-id 0
    :decisions false
    :position-x starting-x
    :position-y starting-y
    :draw-height-offset 20
    :facing :south-west
    :current-action :at-rest
    :run-stamina 300
    }
)
