(ns upstream.config
  (:require [upstream.entities.entitypreset :as entity-preset]
            [upstream.tilemap.tilepreset :as tile-preset])
  (:gen-class))

(import '(java.awt Color Font))

(def HEADLESS-SERVER? (atom false))

(def WINDOW-WIDTH (atom 0)) ;set by dynamic screen maximization
(def WINDOW-HEIGHT (atom 0)) ;set by dynamic screen maximization
(def HEIGHT-BUFFER 100) ;fit doc at bottom of screen
(def VERSION "0.1.0")
(def SERVER-VERSION "0.1.0")
(def WINDOW-TITLE "Upstream")

(def SAVE-FILE "game_saves.txt")
(def AUTO-SAVE-SLEEP 30000) ;Autosave every 30 seconds

(def TILE-SETTINGS
  (atom
  {

  }))
(def ORIGINAL-TILE-WIDTH 64)
(def ORIGINAL-TILE-HEIGHT 32)
(def COMPUTED-SCALE (atom 1))
(def TILES-ACROSS (atom 10))
(def TILES-DOWN (atom 10))

(def STARTING-GAME-STATE {:test 0})
(def LOAD-SCREEN-TTL 100)
(def LOAD-SCREEN-FADE-DIVISION 4)

(def SERVER-LISTEN-PORT 4000)
(def SERVER-DATA-PORTS '(4001 4002 4003 4004))

(def MENU-TEXT-COLOR (Color. 252 144 91))
(def MENU-TEXT-FONT (Font. "Gloucester MT Extra Condensed" Font/PLAIN 60))

(def PLAYER-START-X 100)
(def PLAYER_START-Y 100)

(def LEVEL-ONE-TILEMAPS
  (list
    tile-preset/level-one-layer-0-preset
    tile-preset/level-one-layer-1-preset))

(def LEVEL-ONE-ENTITIES
  (list
    (entity-preset/player-preset-1 PLAYER-START-X PLAYER_START-Y)
    ))
