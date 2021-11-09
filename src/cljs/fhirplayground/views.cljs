(ns fhirplayground.views
  (:require
   [clojure.string :as str]
   [fhirplayground.events :as events]
   [fhirplayground.subs :as subs]
   [fhirplayground.ui :as ui]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [reitit.frontend.easy :as rfe]))

(defn patient
  []
  (let [page-id               @(re-frame/subscribe [::subs/page-id])
        patient-id            @(re-frame/subscribe [::subs/patient-id])
        patient-resource-type @(re-frame/subscribe [::subs/patient-resource-type])
        patient-full-name     @(re-frame/subscribe [::subs/patient-full-name])
        patient-info          @(re-frame/subscribe [::subs/patient-info])]
    [:<>
     [ui/page-header
      {:title    patient-full-name
       :subTitle patient-resource-type
       :onBack   #(.back js/window.history)
       :extra    [(reagent/as-element [ui/popconfirm
                                       {:key       (str (js/Math.random))
                                        :title     "Are you sure you want to delete this Patient?"
                                        :onConfirm (fn []
                                                     (re-frame/dispatch [::events/ws-send
                                                                         {:event-id :client.fhir.delete/patient
                                                                          :payload  {:patient-resource-type patient-resource-type
                                                                                     :patient-id            patient-id
                                                                                     :page-id               page-id}}])
                                                     (re-frame/dispatch [::events/navigate :patients-list]))
                                        :onCancel  #()}
                                       [ui/typography-link
                                        {:key (str (js/Math.random))
                                         :style {:color "#ff4d4f"}}
                                        "Delete"]])
                  (reagent/as-element [ui/typography-link
                                       {:key (str (js/Math.random))}
                                       "Edit"])]
       :avatar   {:src ""}}
      (into [ui/descriptions
             {:title    "Patient Info"
              :bordered true
              :column   4}]
            (map (fn [{:keys [label value]}]
                   [ui/descriptions-item
                    {:key   label
                     :label label
                     :span  2}
                    value])
                 patient-info))]]))

(defn patient-new
  []
  [:<>
   [ui/typography-title "New patient"]])

(defn patients-list
  []
  (let [page-id @(re-frame/subscribe [::subs/page-id])
        ws-msg-replied? @(re-frame/subscribe [::subs/ws-msg-replied? page-id])
        search-results @(re-frame/subscribe [::subs/patients-search-results])]
    [:<>
     [ui/page-header
      {:title    "Patients"
       :subTitle "Administration module"
       :extra    (reagent/as-element [ui/button
                                      {:type "primary"
                                       :disabled true
                                       :icon (reagent/as-element
                                              [ui/user-add-outlined])
                                       :onClick #(re-frame/dispatch [::events/navigate :patient-new])}
                                      "Register new"])
       :footer   (reagent/as-element [ui/input-search
                                      {:placeholder "Enter part or full patient family name for search"
                                       :allowClear  true
                                       :loading     (if ws-msg-replied? false true)
                                       :disabled    (if ws-msg-replied? false true)
                                       :onSearch    #(when (seq %)
                                                       (re-frame/dispatch [::events/set-ws-msg-reply-state
                                                                           {:page-id page-id
                                                                            :reply-state false}])
                                                       (re-frame/dispatch [::events/ws-send
                                                                           {:event-id :client.fhir.search/patient
                                                                            :payload  {:patient-family-name %
                                                                                       :page-id page-id}}]))}])}]
     [ui/simple-list
      {:itemLayout "horizontal"
       :style      {:padding "24px"}
       :header     (reagent/as-element [ui/typography-text
                                        {:strong true}
                                        "Search results:"])
       :loading    (if ws-msg-replied? false true)
       :dataSource search-results
       :pagination {:defaultPageSize 5
                    :pageSizeOptions [5 10 20 30 40 50]
                    :size "small"
                    :showLessItems true
                    :hideOnSinglePage true
                    :showTitle true
                    :showTotal #(str "Total: " %)
                    :disabled (if ws-msg-replied? false true)}
       :renderItem (fn [obj]
                     (let [item (js->clj obj :keywordize-keys true)
                           {:keys [id prefix family-name
                                   given-name gender birthdate]} item]
                       (reagent/as-element
                        [ui/simple-list-item
                         {:key id
                          :actions [(reagent/as-element [ui/typography-link
                                                         {:href (rfe/href :patient {:id id})}
                                                         "View"])]}
                         [ui/simple-list-item-meta
                          {:avatar      (reagent/as-element [ui/avatar
                                                             {:size 40}
                                                             "Patient"])
                           :title       (str/join " " [prefix family-name given-name])
                           :description (str/join " " ["Gender:" gender "|" "Birth date:" birthdate])}]])))}]]))

(defn main-layout
  []
  (let [current-route @(re-frame/subscribe [::subs/current-route])
        loading       @(re-frame/subscribe [::subs/loading])]
    [ui/config-provider
     [ui/layout
      {:style {:min-height "100vh"}}
      [ui/layout-header
       {:style {:width      "100%"
                :background "#f0f2f5"}}
       [:div
        {:style {:fontFamily "\"Libre Barcode 128 Text\", cursive"
                 :fontSize   "40px"
                 :float      "left"
                 :cursor     "default"}}
        "Fhirplayground"]]
      [ui/layout-content
       {:style {:margin "24px auto 24px"
                :width  "70%"}}
       (if loading
         [:div
          {:style {:display         "flex"
                   :justify-content "center"
                   :align-items     "center"}}
          [ui/spin
           {:spinning loading
            :tip      "Loading..."}]]
         (when current-route
           [:div
            {:style {:background "#fff"}}
            [(-> current-route :data :view)]]))]]]))
