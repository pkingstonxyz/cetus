(ns cetus.core
  (:require [cetus.ports.textdb :as txt-db]
            [cetus.ports.textapi :as txt-api]
            [cetus.ports.userdb :as user-db]
            [cetus.pods.texts :as texts]
            [cetus.pods.users :as users]
            [cetus.dock.server :as server]
            [cetus.ports.loggercli :as logger]
            [com.brunobonacci.mulog :as mu]))

(def spaceship (atom {:ports {}
                      :pods  {}}))

;Idk if this is a good way to do a system, but I dislike
; Integrant, Component, Mount, etc. The way you use this is
; by calling start! in the main function.
; There are also restart! and stop! functions and specific
; restart-server! and whatever else might potentially be needed.
; The spaceship is the whole system. The user access it via the dock which
; is why it is at the root level. The server depends ONLY on pods. Pods can
; depend on other pods. Pods can depend on ports. Ports are ways to access
; the outside "stateful" world. Pods are intented to be stateless and full of
; pure functions which then pass their transaction-maps to the ports.
; By convention, ports are named with a suffix of what they are doing and are shortened 
; e.g. user-database -> user-db, text-api -> txt-api.
; By convention, pods are named with a full word of their domain object and their import is 
; not shortened
; By convention, docks are named with a full word of their domain object and their import is 
; not shortened
(defn launch! []
  (println "Launching spaceship...")
  ;;Logging port
  (swap! spaceship assoc-in [:ports :loggercli] (logger/start!))

  ;;texts pod
  (swap! spaceship assoc-in [:ports :text-db]  (txt-db/start!))
  (swap! spaceship assoc-in [:ports :text-api] (txt-api/start!))
  (swap! spaceship assoc-in [:pods  :texts]    (texts/start!))

  ;;users pod
  (swap! spaceship assoc-in [:ports :user-db] (user-db/start!))
  (swap! spaceship assoc-in [:pods  :users]   (users/start!))

  ;;server
  (swap! spaceship assoc :dock (server/start!))

  (println "Spaceship launched!"))

(defn land! []
  (println "Landing spaceship...")
  ;;server
  (server/stop! (:dock @spaceship))
  (swap! spaceship dissoc :dock)

  ;;users pod
  (users/stop! (:users @spaceship))
  (swap! spaceship update-in [:pods] dissoc :users)
  (user-db/stop! (:user-db (:ports @spaceship)))
  (swap! spaceship update-in [:ports] dissoc :user-db)

  ;;texts pod
  (texts/stop! (:texts @spaceship))
  (swap! spaceship update-in [:pods] dissoc :texts)
  (txt-api/stop! (:text-api (:ports @spaceship)))
  (swap! spaceship update-in [:ports] dissoc :text-api)
  (txt-db/stop! (:text-db (:ports @spaceship)))
  (swap! spaceship update-in [:ports] dissoc :text-db)

  ;;logger port
  (logger/stop! (:loggercli (:ports @spaceship)))
  (swap! spaceship update-in [:ports] dissoc :loggercli)
  
  (println "Spaceship landed!"))

(defn redock! []
  (println "Redocking spaceship...")
  (swap! spaceship assoc :dock (server/restart! (:dock @spaceship)))
  (println "Spaceship redocked!"))

(defn -main [& _]
  (launch!)
  (println @spaceship))

(comment
  (launch!)
  (redock!))
