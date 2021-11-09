(ns fhirplayground.handler
  (:require
   [clojure.walk :as walk]
   [fhirplayground.config :as config]
   [fhirplayground.db :as db]
   [fhirplayground.queries :as queries]
   [hiccup.page :as hiccup]
   [jsonista.core :as j]
   [muuntaja.core :as m]
   [reitit.ring :as ring]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.dev :as dev]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [ring.middleware.anti-forgery :as anti-forgery]
   [ring.middleware.cors :as cors]
   [ring.middleware.gzip :as gzip]
   [ring.middleware.keyword-params :as keyword-params]
   [ring.middleware.oauth2 :as oauth2]
   [ring.middleware.session :as session]
   [ring.middleware.session-timeout :as session-timeout]
   [taoensso.timbre :as timbre]))

;; Ring handler

(defn index-page
  []
  (hiccup/html5
   [:head
    [:meta {:charset :utf-8}]
    [:meta {:name    "viewport"
            :content "width=device-width, initial-scale=1.0, shrink-to-fit=no"}]
    [:title "Fhirplayground"]
    [:link {:rel "preconnect" :href "https://fonts.googleapis.com"}]
    [:link {:rel "preconnect" :href "https://fonts.gstatic.com" :crossorigin true}]
    [:link {:rel "stylesheet" :href "https://fonts.googleapis.com/css2?family=Libre+Barcode+128+Text&display=swap"}]
    [:link {:rel "stylesheet" :href "public/css/antd.min.css"}]]
   [:body
    [:div#sente-csrf-token {:data-csrf-token (force anti-forgery/*anti-forgery-token*)}]
    [:div#app]
    [:script {:src "public/js/app_bundle.js"}]
    [:script "fhirplayground.core.init();"]]))

(def ring-handler
  (ring/ring-handler
   (ring/router
    [["/public/*" {:get (ring/create-resource-handler)}]
     ["/cljs-out/*" {:get (ring/create-resource-handler {:root "public/cljs-out"})}]
     ["/" {:get (fn [_]
                  {:status 200
                   :body (index-page)})}]
     ["/chsk" {:get (fn [req]
                      (config/ring-ajax-get-or-ws-handshake req))
               :post (fn [req]
                       (config/ring-ajax-post req))}]
     ["/login" {:get (fn [_]
                       {:status 200
                        :body "Ok"})}]
     ["logout" {:get (fn [_]
                       {:status 200
                        :body "Ok"})}]]
    {;:reitit.middleware/transform dev/print-request-diffs
     :data {:muuntaja m/instance
            :middleware [parameters/parameters-middleware
                         keyword-params/wrap-keyword-params
                         muuntaja/format-negotiate-middleware
                         muuntaja/format-response-middleware
                         exception/exception-middleware
                         muuntaja/format-request-middleware
                         coercion/coerce-request-middleware
                         coercion/coerce-exceptions-middleware
                         coercion/coerce-response-middleware
                         multipart/multipart-middleware
                         [cors/wrap-cors
                          :access-control-allow-origin #".*"
                          :access-control-allow-methods [:get :post]]
                         gzip/wrap-gzip]}})
   (ring/create-default-handler)
   {:middleware [session/wrap-session
                 anti-forgery/wrap-anti-forgery]}))

;; Sente channel socker event message handler

(defn json->keywordized-edn
  [s]
  (-> s
      j/read-value
      walk/keywordize-keys))

(defmulti -event-msg-handler :id)

(defn event-msg-handler
  [ev-msg]
  (future (-event-msg-handler ev-msg)))

(defmethod -event-msg-handler
  :default
  [{:keys [event id ?data ring-req ?reply-fn send-fn]}]
  (let [session (:session ring-req)
        uid     (:uid     session)]
    nil))

(defmethod -event-msg-handler
  :client.fhir.read/patient
  [{:keys [?data]}]
  (let [{:keys [patient-id page-id]} ?data]
    (try
      (let [result (-> (queries/get-patient-by-id
                        @db/datasource
                        {:id patient-id})
                       :resource
                       json->keywordized-edn)]
        (config/chsk-send!
         (first (:any @config/connected-uids))
         [:server.fhir/reply
          {:status      200
           :message     "Received patient data"
           :page-id     page-id
           :interaction :read
           :data        result}]))
      (catch Exception e
        (config/chsk-send!
         (first (:any @config/connected-uids))
         [:server.fhir/reply
          {:status  500
           :message (str "Error occured while reading patient data: "
                         (ex-message e))
           :page-id page-id}])))))

(defn patient-search-entries
  [{:keys [id name gender birthdate]}]
  (let [names (json->keywordized-edn name)]
    {:fullUrl  ""
     :resource {:resourceType "Patient"
                :id           id
                :name         names
                :birthDate    birthdate
                :gender       gender}
     :search   {:mode "match"}}))

(defmethod -event-msg-handler
  :client.fhir.search/patient
  [{:keys [?data]}]
  (let [{:keys [patient-family-name page-id]} ?data]
    (try
      (let [result (queries/search-patient-by-family-name
                    @db/datasource
                    {:family patient-family-name})]
        (config/chsk-send!
         (first (:any @config/connected-uids))
         [:server.fhir/reply
          {:status      200
           :message     "Search completed"
           :page-id     page-id
           :interaction :search
           :data        (assoc {:resourceType "Bundle"
                                :id           "bundle-example"
                                :meta         {:lastUpdated ""}
                                :type         "searchset"
                                :total        nil
                                :link         [{:relation "self"
                                                :url      ""}]}
                               :entries
                               (mapv patient-search-entries result))}]))
      (catch Exception e
        (config/chsk-send!
         (first (:any @config/connected-uids))
         [:server.fhir/reply
          {:status  500
           :message (str "Error occured while searching patient: "
                         (ex-message e))
           :page-id page-id}])))))

(defmethod -event-msg-handler
  :client.fhir.delete/patient
  [{:keys [?data]}]
  (let [{:keys [patient-id patient-resource-type page-id]} ?data]
    (try
      (let [_ (queries/delete-patient
               @db/datasource
               {:patient-id    patient-id
                :resource-type patient-resource-type})]
        (config/chsk-send!
         (first (:any @config/connected-uids))
         [:server.fhir/reply
          {:status      200
           :message     (format "Patient %s has been deleted" patient-id)
           :page-id     page-id
           :interaction :delete
           :data        {:patient-id patient-id}}]))
      (catch Exception e
        (config/chsk-send!
         (first (:any @config/connected-uids))
         [:server.fhir/reply
          {:status  500
           :message (format "Error occured while deleting patient %s: %s"
                            patient-id
                            (ex-message e))
           :page-id page-id}])))))
