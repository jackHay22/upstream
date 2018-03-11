(ns upstream.gamestate.states.menustate
  (:require
    [seesaw.graphics :as sawgr]
    [upstream.gamestate.utils.paralax :as paralax]
    [upstream.utilities.images :as util]
    [upstream.engine.config :as config]
    [seesaw.icon :as sawicon])
  (:gen-class))

(import '(java.awt Color Font))
(def text-color (Color. 191 53 47))
(def text-font (Font. "SansSerif" Font/PLAIN 20))

(def paralax-preset
  ;todo: width not hardcoded
  (list
    {:image (util/load-image "menus/menu_paralax/menu_paralax4x_1.png") :dx 0}
    {:image (util/load-image "menus/menu_paralax/menu_paralax4x_2.png") :dx 0.1}
    {:image (util/load-image "menus/menu_paralax/menu_paralax4x_3.png") :dx 0.5}
    {:image (util/load-image "menus/menu_paralax/menu_paralax4x_4.png") :dx 1}
    {:image (util/load-image "menus/menu_paralax/menu_paralax4x_5.png") :dx 2}))

(paralax/register-layers paralax-preset config/WINDOW-WIDTH)

(def menu-selectable-fields
  (list
    {:y 40 :field "Start"}
    {:y 60 :field "Online"}
    {:y 80 :field "About"}
    {:y 100 :field "Quit"}))

(defn update-and-draw-menu
  "update and draw handler for menu state"
  [gr]
  (paralax/update-layers)
  (paralax/render-layers gr)
        ; (sawgr/draw gr
        ;   (sawgr/image-shape 0 0
        ;     screen-1)
        ;     (sawgr/style))
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
