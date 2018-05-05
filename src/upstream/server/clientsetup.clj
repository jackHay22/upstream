(ns upstream.server.clientsetup
  (:gen-class))

(import javax.swing.JFrame)
(import javax.swing.JTextField)


(defn start-multiplayer-connect-setup
  "start a client to server connection window
  -- returns server, port object"
  []
  (let [new-frame (JFrame. "Connect to Server")
        ip-field (JTextField. 10)
        port-field (JTextField. 10)]
    (reset! internal-debugger? true)
    (reset! debug-window
    (doto new-frame
      (.setAlwaysOnTop true)
      (.setAlwaysOnTop true)
      (.setLocationByPlatform true)
      (.add ip-field BorderLayout/SOUTH)
      (.add port-field BorderLayout/SOUTH)
      (.pack)
      (.setVisible true)))))
