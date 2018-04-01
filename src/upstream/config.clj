(ns upstream.config
  (:require [upstream.entities.entitypreset :as entity-preset])
  (:gen-class))

(import '(java.awt Color Font))

(def HEADLESS-SERVER? (atom false))

(def WINDOW-WIDTH (atom 0)) ;set by dynamic screen maximization
(def WINDOW-HEIGHT (atom 0)) ;set by dynamic screen maximization
(def HEIGHT-BUFFER 100) ;fit doc at bottom of screen
(def VERSION "0.1.0")
(def SERVER-VERSION "0.1.0")
(def WINDOW-TITLE "Upstream")

(def TILES-ACROSS 10) ;base layer (and map unit for all layers)
(def SPACING-STANDARD 64)
(def SPACING-MINIMAL 4)
(def RENDER-STANDARD 0)
(def RENDER-OVERSIZED 1)
(def ORIGINAL-TILE-WIDTH 64)
(def ORIGINAL-TILE-HEIGHT 32)
(def COMPUTED-SCALE (atom 1))

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
    ;if images are loaded using list functionality, factor in indices of previous
    ;images in list for current
    (list
      {:map-path "maps/basic_template.txt"
       :spacing-paradigm SPACING-STANDARD
       :render-optimization RENDER-STANDARD
       :tiles-data (list
                      {:img "tiles/test_sheet.png"
                       :tile-width ORIGINAL-TILE-WIDTH
                       :tile-height ORIGINAL-TILE-HEIGHT})
       :loaded-map-fields (list :image-index :sound)}
      {:map-path "maps/super_block_demo.txt"
       :spacing-paradigm SPACING-STANDARD
       :render-optimization RENDER-OVERSIZED
       :entity-handler? true
       :tiles-data (list
                       {:img "tiles/test_superblock.png"
                        :draw-height-offset 200
                        :tile-width 292
                        :tile-height 270}
                       {:img "tiles/list_load_test.png"
                        :tile-width 64
                        :tile-height 32})
       :loaded-map-fields (list :image-index :height :blocked?)}))

(def LEVEL-ONE-ENTITIES
  (list
    (entity-preset/player-preset-1 PLAYER-START-X PLAYER_START-Y)
    ))
