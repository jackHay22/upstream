(ns upstream.gamestate.utils.menuoptions
  (:require [upstream.utilities.images :as utils])
  (:gen-class))

(def menu-opts (atom '()))

(defn update-currently-selected!
  "update currently selected"
  [index]
  (let [current-state @menu-opts
        all-deselected (map #(assoc % :selected? false) current-state)]
    (reset! menu-opts (map #(if (= index %2) (assoc %1 :selected? true) %1) all-deselected (range)))))

(defn register-menu-options
  "takes list of menu option maps"
  [opt-list]
  (do
    (reset! menu-opts
        (map #(assoc % :selected? false) opt-list))
    (update-currently-selected! 0)))

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
        (utils/draw-image (if (:selected? layer) (:selected layer) (:deselected layer)) gr 0 0)))))

(defn keypressed-menu-option
  "key press handler for menu"
  [key]
  (let [current-state @menu-opts
        state-index (reduce #(if (:selected? %2) (reduced %1) (+ %1 1)) 0 current-state)]
        ;TODO: some problem with :up selection
    (cond (= key :up) (do (update-currently-selected! (rem (- state-index 1) (count current-state))) false)
          (= key :down) (do (update-currently-selected! (rem (+ state-index 1) (count current-state))) false)
          (= key :enter) state-index)))

(defn keyreleased-menu-option
  "key release handler for menu"
  [key])
