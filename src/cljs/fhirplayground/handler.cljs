(ns ^:figwheel-hooks fhirplayground.handler
  (:require
   [fhirplayground.events :as events]
   [fhirplayground.routes :as routes]
   [fhirplayground.views :as views]
   [re-frame.core :as re-frame]
   [reagent.dom :as rdom]))

(defn mount-root
  []
  (let [el (js/document.getElementById "app")]
    (rdom/unmount-component-at-node el)
    (rdom/render [#'views/main-layout] el)))

(defn ^:after-load on-reload
  []
  (re-frame/clear-subscription-cache!)
  (routes/init-routes!)
  (mount-root))

(defmulti event-msg-handler :id)

(defmethod event-msg-handler
  :default
  [{:as ev-msg :keys [event]}]
  nil)

(defmethod event-msg-handler
  :chsk/state
  [{:as ev-msg :keys [event ?data]}]
  (let [[old-state-map new-state-map] ?data]
    (if (:open? new-state-map)
      (do (re-frame/clear-subscription-cache!)
          (re-frame/dispatch-sync [::events/initialize-db])
          (re-frame/dispatch-sync [::events/set-loading false])
          (routes/init-routes!)
          (mount-root))
      (re-frame/dispatch [::events/set-loading true]))))

(defmethod event-msg-handler
  :chsk/recv
  [{:as ev-msg :keys [event ?data]}]
  (let [[event data] ?data]
    (case event
      :server.fhir/reply (re-frame/dispatch [::events/fhir-reply data])
      nil)))

(defmethod event-msg-handler
  :chsk/handshake
  [{:as ev-msg :keys [event ?data]}]
  (let [[?uid ?csrf-token ?handshake-data] ?data]
    nil))
