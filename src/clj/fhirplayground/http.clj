(ns fhirplayground.http
  (:require
   [fhirplayground.handler :as handler]
   [org.httpkit.server :as http-kit]))

(defonce web-server (atom nil))

(defn stop-web-server!
  []
  (when-let [stop-fn @web-server]
    (stop-fn)))

(defn start-web-server!
  [port]
  (stop-web-server!)
  (let [ring-handler (var handler/ring-handler)
        [port stop-fn]
        (let [stop-fn (http-kit/run-server ring-handler {:port port})]
          [(:local-port (meta stop-fn)) (fn [] (stop-fn :timeout 100))])]
    (reset! web-server stop-fn)))
