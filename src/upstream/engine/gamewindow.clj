(ns upstream.engine.gamewindow
  (:gen-class)
  (:require [seesaw.core :as sawcore]
            [seesaw.graphics :as sawgr]
            [upstream.engine.config :as config]
            [upstream.gamestate.gsmanager :as state]))

(def control-keys {java.awt.event.KeyEvent/VK_UP :up
                   java.awt.event.KeyEvent/VK_DOWN :down
                   java.awt.event.KeyEvent/VK_LEFT :left
                   java.awt.event.KeyEvent/VK_RIGHT :right
                   java.awt.event.KeyEvent/VK_SPACE :space
                   java.awt.event.KeyEvent/VK_ENTER :enter
                   java.awt.event.KeyEvent/VK_P :p})

(import java.awt.Toolkit)
(def screenSize (.getScreenSize (Toolkit/getDefaultToolkit)))
(reset! config/WINDOW-WIDTH (.width screenSize))
(reset! config/WINDOW-HEIGHT (- (.height screenSize) 100))

;TODO: reset in config

(defn start-window
  "initialize the game window"
  [title]
  (let [canvas (sawcore/canvas
                  :id :canvas
                  :background :red
                  :size [@config/WINDOW-WIDTH :by @config/WINDOW-HEIGHT]
                  :paint (fn [c g]
                           (state/update-and-draw g)))
        panel (sawcore/vertical-panel
                  :id :panel
                  :items [canvas])
        frame (sawcore/frame
                  :title title
                  :width @config/WINDOW-WIDTH
                  :height @config/WINDOW-HEIGHT
                  :content panel
                  :resizable? false
                  :id :frame
                  :listen [:key-pressed
                            (fn [e] (let [k (.getKeyCode e)]
                                (state/keypressed (control-keys k))))
                           :key-released
                            (fn [e] (let [k (.getKeyCode e)]
                                (state/keyreleased (control-keys k))))]
                  :on-close :exit)
        ;delay 20 is around 50 fps
        main-loop (sawcore/timer (fn [e] (sawcore/repaint! frame)) :delay 20 :start? false)]

    ;run window loop
    (sawcore/native!)
    (sawcore/show! frame)
    (.start main-loop)))
