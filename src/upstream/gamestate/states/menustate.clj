(ns upstream.gamestate.states.menustate
  (:require
    [seesaw.graphics :as sawgr]
    [upstream.utilities.images :as util]
    [seesaw.icon :as sawicon])
  (:gen-class))

(def test-image (util/load-image "tiles/unit_blank2x.png"))
(def screen (util/load-image "menus/splash_temp.png"))

(defn update-and-draw-menu
  "update and draw handler for menu state"
  [gr]
        (sawgr/draw gr
          (sawgr/image-shape 0 0
            screen)
            (sawgr/style))
  )

(defn keypressed-menu
  "key press handler for menu"
  [key]

  )
;
(defn keyreleased-menu
  "key release handler for menu"
  [key]

  )
