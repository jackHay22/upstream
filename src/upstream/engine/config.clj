(ns upstream.engine.config
  (:gen-class))

(import '(java.awt Color Font))

(def WINDOW-WIDTH (atom 1400)) ;set by dynamic screen maximization
(def WINDOW-HEIGHT (atom 800)) ;set by dynamic screen maximization
(def HEIGHT-BUFFER 100)
(def WINDOW-TITLE "Upstream")

;4x multiplier on base resolution
;full screen pixel resolution is 350x200
(def TILE-WIDTH 128)
(def TILE-HEIGHT 64)

(def STARTING-STATE {:test 0})

(def SERVER-LISTEN-PORT 4000)
(def SERVER-DATA-PORTS '(4001 4002 4003 4004))

(def MENU-TEXT-COLOR (Color. 252 144 91))
(def MENU-TEXT-FONT (Font. "Gloucester MT Extra Condensed" Font/PLAIN 60))
