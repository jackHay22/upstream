(ns upstream.entities.entitypreset
  (:gen-class))

(def player-preset-1
  ;Sample Individual Entity State:
  ;each state has a list of cycling animation frames for each of the 8 directions
  {:images {
          :display "entities/logger_1.png"
          :at-rest {
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
    :logical-entity-id 0
    :decisions false
    :position-x 0
    :position-y 0
    :draw-height-offset 20
    :facing :north
    :current-action :at-rest
    }
)
