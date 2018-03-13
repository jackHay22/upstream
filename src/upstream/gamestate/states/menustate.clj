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
    {:image (util/load-image "menus/menu_paralax/paralax4x_0.png") :dx 0}
    {:image (util/load-image "menus/menu_paralax/paralax4x_1.png") :dx 0.2}
    {:image (util/load-image "menus/menu_paralax/paralax4x_2.png") :dx 0.5}
    {:image (util/load-image "menus/menu_paralax/paralax4x_3.png") :dx 0.85}
    {:image (util/load-image "menus/menu_paralax/paralax4x_4.png") :dx 1.35}
    {:image (util/load-image "menus/menu_paralax/paralax4x_5.png") :dx 2.25}))

(defn init-menu
  "init elements"
  []
  (paralax/register-layers paralax-preset config/WINDOW-WIDTH))

(defn update-menu
  "update"
  []
  (do
  (paralax/update-layers)
  true))

(def menu-selectable-fields
  (list
    {:y 40 :field "Start"}
    {:y 60 :field "Online"}
    {:y 80 :field "About"}
    {:y 100 :field "Quit"}))

(defn draw-menu
  "update and draw handler for menu state"
  [gr]
  (paralax/render-layers gr))

(defn keypressed-menu
  "key press handler for menu"
  [key]

  )

(defn keyreleased-menu
  "key release handler for menu"
  [key]

  )
