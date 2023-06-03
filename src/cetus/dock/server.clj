(ns cetus.dock.server
  (:require [org.httpkit.server :as http]

            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.logger :as logger]

            [clojure.core.async :as a]))


(defn my-async-handler 
  [ring-req] 
  (http/as-channel 
    ring-req 
    {:on-open 
     (fn [ch] 
       (println "Opened async") 
       (a/go
         (let [_ (a/<! (a/timeout 5000))]
           (http/send! ch {:status 200 
                           :headers {"Content-Type" "text/html"} 
                           :body "Your async response"}))))}))


(def handler
  (ring/ring-handler
    (ring/router
      ;["/api"
      ; ["/math" {:get {:parameters {:query {:x int?, :y int?}}
      ;                 :responses  {200 {:body {:total int?}}}
      ;                 :handler    (fn [{{{:keys [x y]} :query} :parameters}]
      ;                               {:status 200
      ;                                :body   {:total (+ x y)}})}}]
      [["/hello" {:get {:handler (fn [_] 
                                   {:status 200
                                    :headers {"content-type" "text/html"}
                                    :body "hello, bruh"})}}]
       ["/async" {:get {:handler my-async-handler}}]]
      ;; router data affecting all routes
      {:data {:coercion   reitit.coercion.spec/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware
                           rrc/coerce-request-middleware
                           muuntaja/format-response-middleware
                           rrc/coerce-response-middleware]}})))


(defn start! []
  (http/run-server (logger/wrap-with-logger 
                     handler
                     ;TODO: add log-fn with mulog
                     ) {:port 8080}))

(defn stop! [server] 
  (server :timeout 1000))

(defn restart! [server]
  (stop! server)
  (start!))
