(ns upstream.gamestate.states.menustate
  (:require
    [upstream.gamestate.utils.paralax :as paralax]
    [upstream.gamestate.utils.staticscreen :as screen]
    [upstream.gamestate.utils.menuoptions :as menu]
    [upstream.utilities.images :as util]
    [upstream.config :as config])
  (:gen-class))

(defn load-title-image
  "load title image at init"
  []
  (util/load-image-scale-by-width "menus/menu_title.png" @config/WINDOW-WIDTH))

(defn load-overlay
  []
  (util/load-image-scale-by-width "menus/menu_overlay.png" @config/WINDOW-WIDTH))

(def static-screen-element (atom '()))

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
    {:image (load-scaled "menus/menu_paralax/paralax2_0.png") :dx 0}
    {:image (load-scaled "menus/menu_paralax/paralax2_1.png") :dx 0.2}
    {:image (load-scaled "menus/menu_paralax/paralax2_2.png") :dx 0.4}
    {:image (load-scaled "menus/menu_paralax/paralax2_3.png") :dx 0.5}
    {:image (load-scaled "menus/menu_paralax/paralax2_4.png") :dx 0.6}
    {:image (load-scaled "menus/menu_paralax/paralax2_5.png") :dx 1.6})))

(defn load-menu-selectable-fields
  []
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
  (if (not @config/HEADLESS-SERVER?)
    (do
      ;(screen/clear-registered) ;TODO: fix bug
      (reset! static-screen-element (list
                                      {:image (load-overlay)
                                       :fade? true
                                       :draw? true
                                       :alpha 1
                                       :fade-increment (/ 1.0
                                                          (/ config/LOAD-SCREEN-TTL
                                                             config/LOAD-SCREEN-FADE-DIVISION))}
                                      {:image (load-title-image)
                                       :fade? false
                                       :draw? true}))
      (menu/register-menu-options (load-menu-selectable-fields))
      (paralax/register-layers (load-paralax-preset) @config/WINDOW-WIDTH))))

(defn update-menu
  "update"
  []
  (do
    (reset! static-screen-element (screen/update-alpha-layers @static-screen-element))
    (paralax/update-layers)
    true))

(defn draw-menu
  "update and draw handler for menu state"
  [gr]
  (do
    (paralax/render-layers gr)
    (screen/draw-static-screen-from-preset @static-screen-element gr)
    (menu/draw-menu-options gr)))

(defn keypressed-menu
  "key press handler for menu"
  [key]
  ;TODO: returns integer value to be used by gamestate (0 for arrows, 1 for start, -1 for exit, 2 for about, 3 for multiplayer)
  (menu/keypressed-menu-option key))

(defn keyreleased-menu
  "key release handler for menu"
  [key]

  )
