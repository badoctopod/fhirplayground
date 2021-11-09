(ns fhirplayground.config
  (:require
   [clojure.string :as str]
   [lambdaisland.uri :as uri]
   [taoensso.sente :as sente]
   [taoensso.sente.server-adapters.http-kit :refer (get-sch-adapter)]))

(defn db-spec
  [uri]
  (into {}
        ((fn [{:keys [scheme user password
                      host port path]}]
           {:dbtype   scheme
            :username user
            :password password
            :host     host
            :port     port
            :dbname   (last (str/split path #"/"))})
         (into {} (uri/uri uri)))))

(let [chsk-server
      (sente/make-channel-socket-server!
       (get-sch-adapter) {:packer :edn})
      {:keys [ch-recv send-fn connected-uids
              ajax-post-fn ajax-get-or-ws-handshake-fn]}
      chsk-server]
  (def ring-ajax-post ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk ch-recv)
  (def chsk-send! send-fn)
  (def connected-uids connected-uids))
