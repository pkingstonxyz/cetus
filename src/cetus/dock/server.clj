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


(def clients_ (atom #{})) 

(defn my-async-handler 
  [ring-req] 
  (http/as-channel 
    ring-req 
    {:on-open (fn [ch] (println "added") (swap! clients_ conj ch))
     :on-close (fn [ch status] (println "got rid of") (swap! clients_ disj ch))}))


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
                                    :body "hello"})}}]
       ["/async" {:get {:handler my-async-handler}}]
       ["/send" {:get {:handler (fn [req]
                                  (doseq [ch @clients_] 
                                    (swap! clients_ disj ch) 
                                    (http/send! ch {:status 200 
                                                    :headers {"Content-Type" "text/html"} 
                                                    :body "Your async response"}))
                                  {:status 200
                                   :headers {"content-type" "text/html"}
                                   :body "send"})}}]]
      ;; router data affecting all routes
      {:data {:coercion   reitit.coercion.spec/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware
                           rrc/coerce-request-middleware
                           muuntaja/format-response-middleware
                           rrc/coerce-response-middleware]}})))


(defn start! []
  (http/run-server (logger/wrap-with-logger handler) {:port 8080}))

(defn stop! [server] 
  (server :timeout 1000))
