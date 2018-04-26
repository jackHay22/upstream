(ns upstream.engine.gamewindow
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
(def sleep-ticks-per-second 1000)

(defn graphical-panel
  "-extends JPanel, implements Runnable and KeyListener-
   Run performs active render loop using double buffering
   and dynamically calculates thread sleep for consistent
   framerate"
  [w h s td]
  (let [base-image (BufferedImage. w h BufferedImage/TYPE_INT_ARGB)
        g (cast Graphics2D (.createGraphics base-image))
        window-width (* w s)
        window-height (* h s)]
     (proxy [JPanel Runnable KeyListener] []
            (addNotify []
              (do (proxy-super addNotify)
                  (if (= @system-thread nil)
                      (reset! system-thread (.start (Thread. this))))))
            (keyPressed [e]
              (state/keypressed (control-keys (.getKeyCode e))))
            (keyReleased [e]
              (state/keyreleased (control-keys (.getKeyCode e))))
            (keyTyped [e])
            (paintComponent [^Graphics panel-graphics]
              (proxy-super paintComponent panel-graphics)
              (state/state-draw g)
              (.drawImage panel-graphics base-image 0 0 window-width window-height nil))
            (run [] (loop []
                      (let [render-start (System/nanoTime)]
                      (do (state/state-update)
                          (.repaint this)
                          (Thread/sleep td)))
                    (recur))))))

(defn start-window
  "start JFrame and add JPanel extension as content"
  [title w-resource framerate]
  (let [width (:width w-resource)
        height (:height w-resource)
        scale (:scale w-resource)
        target-delay (/ sleep-ticks-per-second framerate)
        panel (graphical-panel width height scale target-delay)
        window (JFrame. title)]
        (doto panel
          (.setPreferredSize (Dimension. (* width scale) (* height scale)))
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
  (let [frame-delay (int (/ sleep-ticks-per-second framerate))]
  (loop []
    ;TODO: potentially implement better delay calculation
    (state/update-no-draw)
    (Thread/sleep frame-delay)
    (recur))))
