(ns upstream.server.verifier
  (:gen-class))

;NOTE This acts as a game rule and format verifier for client input
; This isn't designed as a load balancer or flow control tool

; TODO: this should operate asyncronously and notify gameloop when ready

; TODO: system should also rebuild entity map in separate thread to avoid clogging up server

(defn client-addr-to-entity-id
  [addr])

(defn verify-packet-formatting
  [p])

(defn verify-packet-user-field
  [p])

(defn verify-packet-actions
  [p])

(defn verify-control-packet
  [packet])
