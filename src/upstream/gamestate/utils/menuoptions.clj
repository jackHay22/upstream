(ns upstream.gamestate.utils.menuoptions
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(def menu-opts (atom '()))
(def current-selection (atom 0))

(defn register-menu-options
  "takes list of menu option maps"
  [opt-list]
  (reset! menu-opts opt-list))

(defn clear-registered
  []
  (reset! menu-opts '()))

(defn draw-menu-options
  "draw options"
  [gr]
  (let [registered @menu-opts]
    (if (not (empty? registered))
      (doseq [layer registered]
        ;use: :selected for selected
        (utils/draw-image (:deselected layer) gr 0 0)))))

(defn keypressed-menu-option
  "key press handler for menu"
  [key]
  ;if enter
  @current-selection)

(defn keyreleased-menu-option
  "key release handler for menu"
  [key])
