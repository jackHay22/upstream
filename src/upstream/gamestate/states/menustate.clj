(ns upstream.gamestate.states.menustate
  (:require
    [seesaw.graphics :as sawgr]
    [upstream.gamestate.utils.paralax :as paralax]
    [upstream.utilities.images :as util]
    [upstream.engine.config :as config]
    [seesaw.icon :as sawicon])
  (:gen-class))

(def title-image (util/load-image "menus/menu_title.png"))

(def paralax-preset
  ;todo: width not hardcoded
  (list
    {:image (util/load-image "menus/menu_paralax/paralax4x_0.png") :dx 0}
    {:image (util/load-image "menus/menu_paralax/paralax4x_1.png") :dx 0.2}
    {:image (util/load-image "menus/menu_paralax/paralax4x_2.png") :dx 0.5}
    {:image (util/load-image "menus/menu_paralax/paralax4x_3.png") :dx 0.85}
    {:image (util/load-image "menus/menu_paralax/paralax4x_4.png") :dx 1.35}
    {:image (util/load-image "menus/menu_paralax/paralax4x_5.png") :dx 2.25}))

(def menu-selectable-fields
  (list
    {:y 425 :field "Start"}
    {:y 525 :field "Online"}
    {:y 625 :field "About"}
    {:y 725 :field "Quit"}))

(defn init-menu
  "init elements"
  []
  (paralax/register-layers paralax-preset @config/WINDOW-WIDTH))

(defn update-menu
  "update"
  []
  (do
  (paralax/update-layers)
  true))


(defn draw-menu-options
  "draw the menu options"
  [gr fields]
  (doseq [option fields]
    (.setColor gr config/MENU-TEXT-COLOR)
    (.setFont gr config/MENU-TEXT-FONT)
    (.drawString gr (:field option) 550 (:y option))))

(defn draw-menu
  "update and draw handler for menu state"
  [gr]
  (paralax/render-layers gr)
  (util/draw-image title-image gr 0 0)
  (draw-menu-options gr menu-selectable-fields)
  )

(defn keypressed-menu
  "key press handler for menu"
  [key]

  )

(defn keyreleased-menu
  "key release handler for menu"
  [key]

  )
