(ns upstream.entities.entitypreset
  (:gen-class))

(def player-preset-1
  ;Sample Individual Entity State:
  ;each state has a list of cycling animation frames for each of the 8 directions
  {:images {
          :display "entities/logger_1.png"
          :at-rest {
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
    :decisions false
    :position-x 0
    :position-y 0
    :facing :north
    :current-action :at-rest
    }
)
