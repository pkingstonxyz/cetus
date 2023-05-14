(ns cetus.core
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [cetus.server :as server]))

(def config
  {:server {:port 3000}})

(defn cetus-system [config]
  (component/system-map
    ;:database (new-database (:database config))
    :server (server/new-server (-> config 
                                   :server 
                                   :port))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (component/start (cetus-system config)))
