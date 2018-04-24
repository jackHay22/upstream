(ns upstream.engine.gamewindowtwo
  (:gen-class)
  (:require [upstream.config :as config]
            [upstream.gamestate.gsmanager :as state]))

(import java.awt.event.KeyListener)
(import java.awt.event.KeyEvent)
(import java.awt.image.BufferedImage)
(import javax.swing.JPanel)
(import javax.swing.JFrame)
(import java.awt.Graphics2D)
(import java.awt.Graphics)
(import java.awt.Dimension)

(def control-keys {java.awt.event.KeyEvent/VK_UP :up
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

(def thread (atom nil))

(defn graphical-panel
  [w h fd]
  (proxy [JPanel Runnable KeyListener] []
            (addNotify []
              (proxy-super addNotify)
              (do
                (if (= nil thread)
                  (.addKeyListener this)
                  (reset! thread (.start (Thread. this))))))
            (keyPressed [e]
              (state/keypressed (control-keys (.getKeyCode e))))
            (keyReleased [e]
              (state/keyreleased (control-keys (.getKeyCode e))))
            (keyTyped [e])
            (run [] (let [base-image (BufferedImage. w h BufferedImage/TYPE_INT_RGB)
                          gr (cast Graphics2D (.getGraphics base-image))]
                      (loop []
                          (let [g (.getGraphics this)
                                start (System/nanoTime)]
                              (do
                                (state/state-update)
                                (state/state-draw g)
                                (.drawImage gr base-image 0 0 w h nil)
                                (.dispose gr)
                                (let [delay (- fd (/ (- (System/nanoTime) start) 1000000))]
                                    (if (> 0 delay)
                                      (Thread/sleep 5)
                                      (Thread/sleep delay))
                                (recur)))))))))

(defn start-window
  "start window"
  [title width height framerate]
  (let [system-thread (Thread.)
        target-delay (/ 1000 framerate)
        panel (graphical-panel width height target-delay)
        window (JFrame. title)]
        (doto panel
                  (.setPreferredSize (Dimension. width height))
                  (.setFocusable true)
                  (.requestFocus))
        (doto window
          (.setContentPane panel)
          (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
          (.setResizable false)
          (.pack)
          (.setVisible true))))

(defn start-headless
  "start update timer without creating window"
  [framerate]
  (let [frame-delay (int (/ 1000 framerate))]
  (loop []
    (state/update-no-draw)
    (Thread/sleep frame-delay)
    (recur))))
