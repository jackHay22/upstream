(ns upstream.config
  (:require [upstream.entities.entitypreset :as entity-preset]
            [upstream.tilemap.tilepreset :as tile-preset])
  (:gen-class))

(import '(java.awt Color Font))

(def HEADLESS-SERVER? (atom false))

(def WINDOW-RESOURCE-WIDTH (atom 0))
(def WINDOW-RESOURCE-HEIGHT (atom 0))

(def HEIGHT-BUFFER 80) ;fit doc at bottom of screen
(def VERSION "0.1.0")
(def SERVER-VERSION "0.1.0")
(def WINDOW-TITLE "Upstream")

(def SAVE-FILE "game_saves.txt")
(def AUTO-SAVE-SLEEP 10000) ;Autosave every 10 seconds

(def ORIGINAL-TILE-WIDTH 64)
(def COMPUTED-SCALE (atom 1))
(def FRAMERATE 60)
;dynamic scale computation
(def TILES-ACROSS 15)

(def LOAD-SCREEN-TTL 100)
(def LOAD-SCREEN-FADE-DIVISION 4)

(def SERVER-LISTEN-PORT 4000)
(def SERVER-DATA-PORTS '(4001 4002 4003 4004))

(def MENU-TEXT-COLOR (Color. 252 144 91))
(def MENU-TEXT-FONT (Font. "Gloucester MT Extra Condensed" Font/PLAIN 60))

(def PLAYER-START-X 700)
(def PLAYER_START-Y 700)
(def WALKING-SPEED 1)
(def RUNNING-SPEED 2)

(def LEVEL-ONE-TILEMAPS
  (list
    tile-preset/level-one-layer-0
    tile-preset/level-one-layer-1))

(def LEVEL-ONE-ENTITIES
  ;Define render as central at front for efficiency
  (list
    (assoc (entity-preset/player-preset-1 PLAYER-START-X PLAYER_START-Y) :render-as-central true)
    ))
