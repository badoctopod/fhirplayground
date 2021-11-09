(ns fhirplayground.subs
  (:require
   [clojure.string :as str]
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::current-route
 (fn [db]
   (:current-route db)))

(re-frame/reg-sub
 ::page-id
 :<- [::current-route]
 (fn [current-route _]
   (-> current-route :data :name)))

(re-frame/reg-sub
 ::ws-msg-replied?
 (fn [db [_ page-id]]
   (get-in db [:pages page-id :ws-msg-reply-state] true)))

(re-frame/reg-sub
 ::patient
 (fn [db]
   (get-in db [:pages :patient
               :interactions :read])))

(re-frame/reg-sub
 ::patient-resource-type
 :<- [::patient]
 (fn [patient _]
   (:resourceType patient)))

(re-frame/reg-sub
  ::patient-id
  :<- [::patient]
  (fn [patient _]
    (:id patient)))

(re-frame/reg-sub
 ::patient-full-name
 :<- [::patient]
 (fn [patient _]
   (let [name   (-> patient :name first)
         family (:family name)
         given  (-> name :given first)
         prefix (-> name :prefix first)]
     (str/join " " [prefix family given]))))

(re-frame/reg-sub
 ::patient-birthdate
 :<- [::patient]
 (fn [patient _]
   {:label "Birth date"
    :value (:birthDate patient)}))

(re-frame/reg-sub
 ::patient-gender
 :<- [::patient]
 (fn [patient _]
   {:label "Gender"
    :value (:gender patient)}))

(re-frame/reg-sub
 ::patient-telecom
 :<- [::patient]
 (fn [patient _]
   (let [telecom (-> patient :telecom first)
         {:keys [use system value]} telecom]
     {:label (str/capitalize (str/join " " [use system]))
      :value value})))

(re-frame/reg-sub
 ::patient-address
 :<- [::patient]
 (fn [patient _]
   (let [address (-> patient :address first)
         {:keys [country city postalCode state]} address
         line (-> address :line first)]
     {:label "Address"
      :value (str/join ", " [line city state postalCode country])})))

(re-frame/reg-sub
 ::patient-identifiers
 :<- [::patient]
 (fn [patient _]
   (let [identifiers (filter
                      (fn [{:keys [type]}]
                        (some? type))
                      (:identifier patient))]
     (mapv (fn [{:keys [value] {:keys [text]} :type}]
             {:label text
              :value value})
           identifiers))))

(re-frame/reg-sub
 ::patient-info
 :<- [::patient-gender]
 :<- [::patient-birthdate]
 :<- [::patient-address]
 :<- [::patient-telecom]
 :<- [::patient-identifiers]
 (fn [[gender birthdate address telecom identifiers] _]
   (flatten (conj [gender birthdate telecom address] identifiers))))

(re-frame/reg-sub
 ::patients-search-results
 (fn [db]
   (let [resources (map :resource
                        (get-in db [:pages :patients-list
                                    :interactions :search
                                    :entries]))]
     (mapv (fn [{:keys [id name birthDate gender]}]
             {:id id
              :birthdate birthDate
              :gender gender
              :family-name (:family name)
              :given-name (-> name :given first)
              :prefix (-> name :prefix first)})
           resources))))

(re-frame/reg-sub
 ::loading
 (fn [db]
   (:loading db)))
