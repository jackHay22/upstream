(ns upstream.gamestate.states.menustate
  (:require
    [seesaw.graphics :as sawgr]
    [upstream.gamestate.utils.paralax :as paralax]
    [upstream.utilities.images :as util]
    [upstream.engine.config :as config]
    [seesaw.icon :as sawicon])
  (:gen-class))

;TODO: scaling?
(def title-image (util/load-image-scale-by-width "menus/menu_title.png" @config/WINDOW-WIDTH))

(def start? (atom false))
(def about? (atom false))
(def online? (atom false))
(def quit? (atom false))

(defn load-paralax-preset
  "build paralax set with new width"
  []
  (let [width @config/WINDOW-WIDTH
        load-scaled (fn [img]
            (util/load-image-scale-by-width img width))]
  (list
    {:image (load-scaled "menus/menu_paralax/paralax_0.png") :dx 0}
    {:image (load-scaled "menus/menu_paralax/paralax_1.png") :dx 0.2}
    {:image (load-scaled "menus/menu_paralax/paralax_2.png") :dx 0.5}
    {:image (load-scaled "menus/menu_paralax/paralax_3.png") :dx 0.85}
    {:image (load-scaled "menus/menu_paralax/paralax_4.png") :dx 1.35}
    {:image (load-scaled "menus/menu_paralax/paralax_5.png") :dx 2.25})))

(def menu-selectable-fields
  (let [width @config/WINDOW-WIDTH
        load-scaled (fn [img]
            (util/load-image-scale-by-width img width))]
  (list
    {:selected   (load-scaled "menus/optiontext/start_selected.png")
     :deselected (load-scaled "menus/optiontext/start_deselected.png")}
    {:selected   (load-scaled "menus/optiontext/online_selected.png")
     :deselected (load-scaled "menus/optiontext/online_deselected.png")}
    {:selected   (load-scaled "menus/optiontext/about_selected.png")
     :deselected (load-scaled "menus/optiontext/about_deselected.png")}
    {:selected   (load-scaled "menus/optiontext/quit_selected.png")
     :deselected (load-scaled "menus/optiontext/quit_deselected.png")})))

(defn init-menu
  "init elements"
  []
  (paralax/register-layers (load-paralax-preset) @config/WINDOW-WIDTH))

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
    (util/draw-image (:deselected option) gr 0 0)))

(defn draw-menu
  "update and draw handler for menu state"
  [gr]
  (paralax/render-layers gr)
  (util/draw-image title-image gr 0 0)
  ;(draw-menu-options gr menu-selectable-fields)
  ;TODO: needs to have updated width
  )

(defn keypressed-menu
  "key press handler for menu"
  [key]

  )

(defn keyreleased-menu
  "key release handler for menu"
  [key]

  )
