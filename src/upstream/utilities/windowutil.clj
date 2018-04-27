(ns upstream.utilities.windowutil
  (:gen-class))

(import java.awt.Toolkit)
(import java.awt.GraphicsDevice)
(import java.awt.GraphicsEnvironment)

(defn compute-window-resource
  "compute screen size resource"
  [resource-total-width]
  (let [toolkit (Toolkit/getDefaultToolkit)
        device-graphics (.getDefaultScreenDevice (GraphicsEnvironment/getLocalGraphicsEnvironment))
        screenSize (.getScreenSize toolkit)
        doc-inset (.getScreenInsets toolkit (.getDefaultConfiguration device-graphics))
        scale (/ (.width screenSize) resource-total-width) ;.getBounds()
        resource-total-height (int (/ (.height screenSize) scale))] ;TODO  doc-inset/bottom)
    (hash-map :width resource-total-width
              :height (- resource-total-height 80) ;TODO: figure out the actual size of the inset
              :scale scale)))
