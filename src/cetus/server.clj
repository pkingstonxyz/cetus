(ns cetus.server
  (:require [reitit.ring :as ring]
            [reitit.coercion.malli]
            [reitit.openapi :as openapi]
            [reitit.ring.malli]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.dev.pretty :as pretty]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [malli.util :as mu]))

(def app
  (ring/ring-handler
    (ring/router
      [["/openapi/openapi.json"
        {:get {:no-doc true
               :openapi {:info {:title "my-api"
                                :description "openapi3 docs with [malli](https://github.com/metosin/malli) and reitit-ring"
                                :version "0.0.1"}}
               :handler (openapi/create-openapi-handler)}}]
       ["/math"
        {:tags ["math"]}
        ["/plus"
         {:get {:summary "plus with malli query parameters"
                :parameters {:query [:map
                                     [:x
                                      {:title "X parameter"
                                       :description "Description for X parameter"
                                       :json-schema/default 42}
                                      int?]
                                     [:y int?]]}
                :responses {200 {:body [:map [:total int?]]}}
                :handler (fn [{{{:keys [x y]} :query} :parameters}]
                           {:status 200
                            :body {:total (+ x y)}})}
          :post {:summary "plus with malli body parameters"
                 :parameters {:body [:map
                                     [:x
                                      {:title "X parameter"
                                       :description "Description for X parameter"
                                       :json-schema/default 42}
                                      int?]
                                     [:y int?]]}
                 ;; OpenAPI3 named examples for request & response
                 :openapi {:requestBody
                           {:content
                            {"application/json"
                             {:examples {"add-one-one" {:summary "1+1"
                                                        :value {:x 1 :y 1}}
                                         "add-one-two" {:summary "1+2"
                                                        :value {:x 1 :y 2}}}}}}
                           :responses
                           {200
                            {:content
                             {"application/json"
                              {:examples {"two" {:summary "2"
                                                 :value {:total 2}}
                                          "three" {:summary "3"
                                                   :value {:total 3}}}}}}}}
                 :responses {200 {:body [:map [:total int?]]}}
                 :handler (fn [{{{:keys [x y]} :body} :parameters}]
                            {:status 200
                             :body {:total (+ x y)}})}}]]]
      {:exception pretty/exception
       :data {:coercion (reitit.coercion.malli/create
                          {;; set of keys to include in error messages
                           :error-keys #{#_:type :coercion :in :schema :value :errors :humanized #_:transformed}
                           ;; schema identity function (default: close all map schemas)
                           :compile mu/closed-schema
                           ;; strip-extra-keys (effects only predefined transformers)
                           :strip-extra-keys true
                           ;; add/set default values
                           :default-values true
                           ;; malli options
                           :options nil})
              :muuntaja m/instance
              :middleware [;; openapi
                           openapi/openapi-feature
                           ;; query-params & form-params
                           parameters/parameters-middleware
                           ;; content-negotiation
                           muuntaja/format-negotiate-middleware
                           ;; encoding response body
                           muuntaja/format-response-middleware
                           ;; exception handling
                           exception/exception-middleware
                           ;; decoding request body
                           muuntaja/format-request-middleware
                           ;; coercing response bodys
                           coercion/coerce-response-middleware
                           ;; coercing request parameters
                           coercion/coerce-request-middleware
                           ;; multipart
                           multipart/multipart-middleware]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/openapi"
         :config {:validatorUrl nil
                  :urls [{:name "openapi", :url "openapi.json"}]
                  :urls.primaryName "openapi"
                  :operationsSorter "alpha"}})
      (ring/create-default-handler))))

(defn start! []
  (jetty/run-jetty #'app {:port 3000, :join? false})
  (println "server running in port 3000"))

