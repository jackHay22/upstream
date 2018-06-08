(ns upstream.entities.entitypreset
  (:gen-class))

(defrecord StateCollection [north north-east
                            east south-east
                            south south-west
                            west north-west])

(defrecord AnimationSheet [resource resource-width offset-x offset-y])

(defn player-preset-1
  "take x,y and return preset"
  [starting-x starting-y]
  ;Sample Individual Entity State:
  ;each state has a list of cycling animation frames for each of the 8 directions
  {:images {
         :walking (StateCollection. (AnimationSheet. "entities/n_walk.png" 0 0 0) (AnimationSheet. "entities/ne_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/e_walk.png" 80 34 122) (AnimationSheet. "entities/se_walk.png" 80 37 115)
                                    (AnimationSheet. "entities/s_walk.png" 0 0 0) (AnimationSheet. "entities/sw_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/w_walk.png" 0 0 0) (AnimationSheet. "entities/nw_walk.png" 0 0 0))
         :at-rest (StateCollection. (AnimationSheet. "entities/n_walk.png" 0 0 0) (AnimationSheet. "entities/ne_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/e_walk.png" 80 34 122) (AnimationSheet. "entities/se_walk.png" 80 37 115)
                                    (AnimationSheet. "entities/s_walk.png" 0 0 0) (AnimationSheet. "entities/sw_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/w_walk.png" 0 0 0) (AnimationSheet. "entities/nw_walk.png" 0 0 0))
         :running (StateCollection. (AnimationSheet. "entities/n_walk.png" 0 0 0) (AnimationSheet. "entities/ne_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/e_walk.png" 80 34 122) (AnimationSheet. "entities/se_walk.png" 80 37 115)
                                    (AnimationSheet. "entities/s_walk.png" 0 0 0) (AnimationSheet. "entities/sw_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/w_walk.png" 0 0 0) (AnimationSheet. "entities/nw_walk.png" 0 0 0))
         :punching (StateCollection. (AnimationSheet. "entities/n_walk.png" 0 0 0) (AnimationSheet. "entities/ne_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/e_walk.png" 80 34 122) (AnimationSheet. "entities/se_walk.png" 80 37 115)
                                    (AnimationSheet. "entities/s_walk.png" 0 0 0) (AnimationSheet. "entities/sw_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/w_walk.png" 0 0 0) (AnimationSheet. "entities/nw_walk.png" 0 0 0))
         :static-jumping (StateCollection. (AnimationSheet. "entities/n_walk.png" 0 0 0) (AnimationSheet. "entities/ne_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/e_walk.png" 80 34 122) (AnimationSheet. "entities/se_walk.png" 80 37 115)
                                    (AnimationSheet. "entities/s_walk.png" 0 0 0) (AnimationSheet. "entities/sw_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/w_walk.png" 0 0 0) (AnimationSheet. "entities/nw_walk.png" 0 0 0))
         :run-jumping (StateCollection. (AnimationSheet. "entities/n_walk.png" 0 0 0) (AnimationSheet. "entities/ne_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/e_walk.png" 80 34 122) (AnimationSheet. "entities/se_walk.png" 80 37 115)
                                    (AnimationSheet. "entities/s_walk.png" 0 0 0) (AnimationSheet. "entities/sw_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/w_walk.png" 0 0 0) (AnimationSheet. "entities/nw_walk.png" 0 0 0))
         :walk-jumping (StateCollection. (AnimationSheet. "entities/n_walk.png" 0 0 0) (AnimationSheet. "entities/ne_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/e_walk.png" 80 34 122) (AnimationSheet. "entities/se_walk.png" 80 37 115)
                                    (AnimationSheet. "entities/s_walk.png" 0 0 0) (AnimationSheet. "entities/sw_walk.png" 0 0 0)
                                    (AnimationSheet. "entities/w_walk.png" 0 0 0) (AnimationSheet. "entities/nw_walk.png" 0 0 0))
    }
    :all-states (list :at-rest :walking :running :punching :walk-jumping :static-jumping :run-jumping)
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
    :position-z 0
    :height-dz 0
    :collision-diameter 30
    :facing :south-west ;TODO remove?
    :current-action :at-rest
    :frame-index 0
    :run-stamina 300 })
