(ns upstream.entities.entitypreset
  (:gen-class))

(def player-preset
  ;Sample Individual Entity State:
  {:images {
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
    :decisions "" ;Maybe not for standard player
    :position-x 0
    :position-y 0
    }
)
