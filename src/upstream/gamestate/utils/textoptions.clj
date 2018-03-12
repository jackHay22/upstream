(ns upstream.gamestate.utils.textoptions
  (:require
    [seesaw.graphics :as sawgr]
    ;[upstream.gamestate.gsmanager :as manager] ;figure out way to change state number on event
    [seesaw.icon :as sawicon])
  (:gen-class))

(def textoptions-state (atom '()))
(def selected-option 0)
(def bg-image (atom nil))

(defn register-options
  "take list of maps that include text, y location and handler fn"
  [text-option-maps]
  (reset! textoptions-state text-option-maps))

(defn register-options-background
  "takes image file that is drawn under list options"
  [bg]
  (reset! bg-image bg))

(defn option-select-event
  []
  (:fn (deref textoptions-state)))

(defn option-change-event
  "change selected option based on input"
  [dir]
  ;(reset! selected-option (if (= dir :up) )) ;TODO: use mod/rem correctly to change selection
  )

(defn render-options
  "display all text options"
  [gr]
    ; (let [state (deref game-state)
    ;       score (str (:score-1 state) " --- " (:score-2 state))]
    ;     (.setColor gr text-color)
    ;     (.setFont gr score-font)
    ;     (.drawString gr score 465 22))
        )
