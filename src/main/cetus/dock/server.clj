(ns cetus.dock.server
  (:require [org.httpkit.server :as http]

            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.logger :as logger]
            [com.brunobonacci.mulog :as mu]

            [clojure.core.async :as a]

            [cetus.pods.texts :as texts]))

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
    [texts/routes
     ["/test"
      ["/math" {:get {:parameters {:query {:x int?, :y int?}}
                      :responses  {200 {:body {:total int?}}}
                      :handler    (fn [{{{:keys [x y]} :query} :parameters}]
                                    {:status 200
                                     :body   {:total (+ x y)}})}}]
      ["/hello" {:get {:handler (fn [_]
                                  {:status 200
                                   :headers {"content-type" "text/html"}
                                   :body "hello, bruh"})}}]
      ["/async" {:get {:handler my-async-handler}}]]]
      ;; router data affecting all routes
    {:data {:coercion   reitit.coercion.spec/coercion
            :muuntaja   m/instance
            :middleware [parameters/parameters-middleware
                         rrc/coerce-request-middleware
                         muuntaja/format-response-middleware
                         rrc/coerce-response-middleware]}})
   (ring/create-default-handler)))

(defn start! []
  (http/run-server (logger/wrap-with-logger
                    handler
                    {:log-fn
                     (fn [f]
                       (mu/log (:ring.logger/type (:message f))
                               :uri (:uri (:message f))
                               :method (:request-method (:message f))
                               :status (:status (:message f))
                               :ms (:ring.logger/ms (:message f))))})
                   {:port 8080}))

(defn stop! [server]
  (server :timeout 1000))

(defn restart! [server]
  (stop! server)
  (start!))

(comment
  (cetus.core/redock!))
