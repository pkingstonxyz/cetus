(ns cetus.dock.server
  (:require [aleph.http :as aleph]

            [muuntaja.core :as m]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.ring.coercion :as rrc]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.logger :as logger]

            [manifold.deferred :as d]
            [manifold.stream :as s]
            [clojure.core.async :as a]))

(defn hello-world-handler
  [req]
  {:status 200
   :headers {"content-type" "text/plain"}
   :body "hello world!"})

(defn delayed-hello-world-handler
  [req]
  (s/take!
    (s/->source
      (a/go
        (let [_ (a/<! (a/timeout 1000))]
          (hello-world-handler req))))))

(defn streaming-numbers-handler
  [{:keys [params]}]
  (let [cnt (Integer/parseInt (get params "count" "0"))]
    {:status 200
     :headers {"content-type" "text/plain"}
     :body (let [sent (atom 0)]
             (->> (s/periodically 100 #(str (swap! sent inc) "\n"))
                  (s/transform (take cnt))))}))

(def handler
  (ring/ring-handler
    (ring/router
      ;["/api"
      ; ["/math" {:get {:parameters {:query {:x int?, :y int?}}
      ;                 :responses  {200 {:body {:total int?}}}
      ;                 :handler    (fn [{{{:keys [x y]} :query} :parameters}]
      ;                               {:status 200
      ;                                :body   {:total (+ x y)}})}}]
      [["/hello"        {:get {:handler hello-world-handler}}]
       ["/hellodelayed" {:get {:handler delayed-hello-world-handler}}]
       ["/numbers" {:get {:handler streaming-numbers-handler}}]]
      ;; router data affecting all routes
      {:data {:coercion   reitit.coercion.spec/coercion
              :muuntaja   m/instance
              :middleware [parameters/parameters-middleware
                           rrc/coerce-request-middleware
                           muuntaja/format-response-middleware
                           rrc/coerce-response-middleware]}})))


(defn start! []
  (aleph/start-server (logger/wrap-with-logger handler) {:port 8080}))

(defn stop! [server] 
  (.close server))
