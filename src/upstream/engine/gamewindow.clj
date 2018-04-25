(ns upstream.engine.gamewindow
  (:gen-class)
  (:require [seesaw.core :as sawcore]
            [seesaw.graphics :as sawgr]
            [upstream.config :as config]
            [upstream.gamestate.gsmanager :as state]))

(import java.awt.event.KeyListener)
(import java.awt.event.KeyEvent)
(import java.awt.image.BufferedImage)
(import javax.swing.JPanel)
(import javax.swing.JFrame)
(import java.awt.Graphics2D)
(import java.awt.Graphics)
(import java.awt.Dimension)

(def control-keys
      {java.awt.event.KeyEvent/VK_UP :up
       java.awt.event.KeyEvent/VK_DOWN :down
       java.awt.event.KeyEvent/VK_LEFT :left
       java.awt.event.KeyEvent/VK_RIGHT :right
       java.awt.event.KeyEvent/VK_SPACE :space
       java.awt.event.KeyEvent/VK_ENTER :enter
       java.awt.event.KeyEvent/VK_SHIFT :shift
       java.awt.event.KeyEvent/VK_P :p
       java.awt.event.KeyEvent/VK_S :s
       java.awt.event.KeyEvent/VK_L :l
       java.awt.event.KeyEvent/VK_R :r})

(def system-thread (atom nil))

(defn graphical-panel
  [w h fd]
  (let [base-image (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)]
  (proxy [JPanel Runnable KeyListener] []
            (addNotify []
              (do
                (proxy-super addNotify)
                (if (= @system-thread nil)
                    (reset! system-thread (.start (Thread. this))))))
            (keyPressed [e]
              (state/keypressed (control-keys (.getKeyCode e))))
            (keyReleased [e]
              (state/keyreleased (control-keys (.getKeyCode e))))
            (keyTyped [e])
            (run [] (loop []
                      (let [render-start (System/nanoTime)]
                      (do
                        (state/state-update)
                        (state/state-draw (.getGraphics base-image))
                        (let [gr (.getGraphics this)]
                          (.drawImage gr base-image 0 0 nil)
                          (.dispose gr))
                        (let [render-elapsed (- (System/nanoTime) render-start)
                              frame-delay (- fd (/ render-elapsed 1000000))
                              actual-delay (if (> 0 frame-delay) 5 frame-delay)]
                        (Thread/sleep actual-delay))))
                      (recur))))))

(defn start-window
  "start window"
  [title width height framerate]
  (let [target-delay (/ 1000 framerate)
        panel (graphical-panel width height target-delay)
        window (JFrame. title)]
        (doto panel
          (.setPreferredSize (Dimension. width height))
          (.setFocusable true)
          (.requestFocus)
          (.addKeyListener panel))
        (doto window
          (.setContentPane panel)
          (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
          (.setResizable false)
          (.pack)
          (.setVisible true)
          (.validate)
          (.repaint))))

(defn start-headless
  "start update timer without creating window"
  [framerate]
  (let [frame-delay (int (/ 1000 framerate))]
  (loop []
    (state/update-no-draw)
    (Thread/sleep frame-delay)
    (recur))))
