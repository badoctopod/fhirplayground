(ns fhirplayground.events
  (:require
   [fhirplayground.config :as config]
   [fhirplayground.db :as db]
   [fhirplayground.ui :as ui]
   [re-frame.core :as re-frame]
   [reitit.frontend.controllers :as rfc]
   [reitit.frontend.easy :as rfe]
   [taoensso.timbre :as timbre]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::navigate
 (fn [_ [_ & route]]
   {::push-state route}))

(re-frame/reg-event-db
 ::navigated
 (fn [db [_ new-match]]
   (let [old-match (:current-route db)
         controllers (rfc/apply-controllers (:controllers old-match)
                                            new-match)]
     (assoc db
            :current-route
            (assoc new-match :controllers controllers)))))

(re-frame/reg-fx
 ::push-state
 (fn [route]
   (apply rfe/push-state route)))

(re-frame/reg-event-db
 ::clear-page-state
 (fn [db [_ page-id]]
   (update-in db [:pages] dissoc page-id)))

(re-frame/reg-event-db
 ::set-loading
 (fn [db [_ state]]
   (assoc db :loading state)))

(re-frame/reg-event-fx
 ::ws-send
 (fn [{:keys [db]} [_ data]]
   {:fx [[::ws-send! data]]}))

(re-frame/reg-fx
 ::ws-send!
 (fn [{:keys [event-id payload]}]
   (config/chsk-send! [event-id payload])))

(re-frame/reg-event-db
 ::set-ws-msg-reply-state
 (fn [db [_ {:keys [page-id reply-state]}]]
   (assoc-in db [:pages page-id :ws-msg-reply-state] reply-state)))

(re-frame/reg-event-fx
 ::fhir-reply
 (fn [{:keys [db]} [_ {:keys [status message page-id interaction data] :as ev-msg}]]
   (case status
     200 {:db (assoc-in db
                        [:pages page-id :interactions interaction]
                        data)
          :fx [[:dispatch [::set-ws-msg-reply-state {:page-id page-id
                                                     :reply-state true}]]
               [::show-message! ev-msg]]}
     500 {:fx [[:dispatch [::set-ws-msg-reply-state {:page-id page-id
                                                     :reply-state true}]]
               [::show-message! ev-msg]]})))

(re-frame/reg-fx
 ::show-message!
 (fn [{:keys [status message page-id interaction] :as ev-msg}]
   (case status
     200 (.success ui/show-message message)
     500 (.error ui/show-message message))))
