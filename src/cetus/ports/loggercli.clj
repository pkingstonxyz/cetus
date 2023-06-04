(ns cetus.ports.loggercli
  (:require [com.brunobonacci.mulog :as mu]))

(defn start! []
  (mu/start-publisher! {:type :console}))

(defn stop! [_] :ok)
