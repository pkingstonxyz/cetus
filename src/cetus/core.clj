(ns cetus.core
  (:gen-class)
  (:require [cetus.server :as server]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (server/start!))
