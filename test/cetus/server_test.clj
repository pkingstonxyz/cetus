(ns cetus.server-test
  (:require [clojure.test :as t]
            [cetus.server :as server]
            [ring.mock.request :as ring]))

(t/deftest example-server
  (t/testing "GET"
    (t/is (= (-> (ring/request :get "/math/plus?x=20&y=3")
                 server/app 
                 :body 
                 slurp)
             (-> {:request-method :get :uri "/math/plus" :query-string "x=20&y=3"}
                 server/app 
                 :body 
                 slurp)
             (-> {:request-method :get :uri "/math/plus" :query-params {:x 20 :y 3}}
                 server/app 
                 :body 
                 slurp)
             "{\"total\":23}")))

  (t/testing "POST"
    (t/is (= (-> (ring/request :post "/math/plus") 
                 (ring/json-body {:x 40 :y 2})
                 server/app 
                 :body 
                 slurp)
             (-> {:request-method :post :uri "/math/plus" :body-params {:x 40 :y 2}}
                 server/app 
                 :body 
                 slurp)
             "{\"total\":42}"))))


