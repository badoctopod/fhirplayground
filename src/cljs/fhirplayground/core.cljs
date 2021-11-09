(ns fhirplayground.core
  (:require
   [fhirplayground.config :as config]
   [fhirplayground.websocket :as ws]
   [taoensso.timbre :as timbre]))

(set! *warn-on-infer* true)

(defn setup
  []
  (if config/debug?
    (do (timbre/set-level! :debug)
        (enable-console-print!))
    (timbre/set-level! :info)))

(defn ^:export init
  []
  (setup)
  (ws/start-router!))
