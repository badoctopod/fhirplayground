(ns fhirplayground.config
  (:require
   [taoensso.sente :as sente]))

(defonce debug? ^boolean goog.DEBUG)

(def ?csrf-token
  (when-let [el (.getElementById js/document "sente-csrf-token")]
    (.getAttribute el "data-csrf-token")))

(let [{:keys [chsk ch-recv send-fn state]}
      (sente/make-channel-socket-client!
       "/chsk"
       ?csrf-token
       {:type   :auto
        :packer :edn})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv)
  (def chsk-send! send-fn)
  (def chsk-state state))
