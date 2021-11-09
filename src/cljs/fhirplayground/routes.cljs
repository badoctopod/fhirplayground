(ns fhirplayground.routes
  (:require
   [fhirplayground.events :as events]
   [fhirplayground.views :as views]
   [re-frame.core :as re-frame]
   [reitit.coercion.spec :as rcs]
   [reitit.frontend :as rf]
   [reitit.frontend.easy :as rfe]))

(defn clear-page-state
  [page-id]
  (re-frame/dispatch [::events/clear-page-state page-id]))

(def routes
  ["/"
   [""
    {:name        :patients-list
     :view        views/patients-list
     :controllers [{:start (fn [& params])
                    :stop  (fn [& params]
                             (clear-page-state :patients-list))}]}]
   ["patient"
    ["/new"
     {:name        :patient-new
      :view        views/patient-new
      :controllers [{:start (fn [_])
                     :stop  (fn [_])}]}]
    ["/id/:id"
     {:name        :patient
      :view        views/patient
      :controllers [{:parameters {:path [:id]}
                     :start      (fn [{:keys [path]}]
                                   (re-frame/dispatch [::events/ws-send
                                                       {:event-id :client.fhir.read/patient
                                                        :payload  {:patient-id (:id path)
                                                                   :page-id    :patient}}]))
                     :stop       (fn [& params]
                                   (clear-page-state :patient))}]}]]])

(defn on-navigate
  [new-match]
  (when new-match
    (re-frame/dispatch [::events/navigated new-match])))

(def router
  (rf/router routes {:data {:coercion rcs/coercion}}))

(defn init-routes!
  []
  (rfe/start! router on-navigate {:use-fragment true}))

















