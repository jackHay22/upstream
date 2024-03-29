(ns upstream.config
  (:require [upstream.entities.entitypreset :as entity-preset]
            [upstream.tilemap.tilepreset :as tile-preset])
  (:gen-class))

(import '(java.awt Color Font))

(def HEADLESS-SERVER? (atom false))

(def WINDOW-RESOURCE-WIDTH (atom 0))
(def WINDOW-RESOURCE-HEIGHT (atom 0))

(def HEIGHT-BUFFER 80) ;fit doc at bottom of screen
(def VERSION "0.1.3")
(def SERVER-VERSION "0.1.0")
(def WINDOW-TITLE "Upstream")

(def SAVE-FILE "game_saves.txt")
(def AUTO-SAVE-SLEEP 10000) ;Autosave every 10 seconds

(def ORIGINAL-TILE-WIDTH 64)
(def COMPUTED-SCALE (atom 1))
(def FRAMERATE 60)
(def ANIMATION-FRAME-DELAY 6)
;dynamic scale computation
(def TILES-ACROSS 15)

(def DYNAMIC-STORAGE-DIM 4) ;chunks to retain in memory from dynamically loaded file

(def LOAD-SCREEN-TTL 100)
(def LOAD-SCREEN-FADE-DIVISION 4)

(def SERVER-LISTEN-PORT 4000)
(def SERVER-DATA-PORTS '(4001 4002 4003 4004))

(def MENU-TEXT-COLOR (Color. 252 144 91)) ;TODO: remove? (or test as non image menu options)
(def MENU-TEXT-FONT (Font. "Gloucester MT Extra Condensed" Font/PLAIN 60))

(def PLAYER-START-X 850)
(def PLAYER_START-Y 850)
(def WALKING-SPEED 0.7)
(def RUNNING-SPEED 2)

(def GRAVITY-PER-FRAME 0.2)
(def JUMP-MAGNITUDE 2)

(def LEVEL-ONE-TILEMAPS
  (list
    tile-preset/level-one-layer-0
    tile-preset/level-one-layer-1
    ;tile-preset/level-one-layer-2 ;NOTE: java literally runs out of heap space
    ))

(def LEVEL-ONE-ENTITIES
  ;Define render as central at front for efficiency
  (list ;NOTE: when running offline, main character must be first in state list
        ;for correct key press association
    (entity-preset/player-preset-1 PLAYER-START-X PLAYER_START-Y)
    ))

 (def ONLINE-ENTITIES
  ; (list entity-preset/ai-preset-1)
   )
