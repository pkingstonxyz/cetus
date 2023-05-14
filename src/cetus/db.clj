(ns cetus.db
  (:require [datalevin.core :as dlv]))

(def db (dlv/open-kv "./data"))

(def users-table "users-table")

(dlv/open-dbi db users-table)

(dlv/transact-kv db 
 [[:put users-table "foo" :bar]
  [:put users-table "bar" #{:name "Mary" :age 25}]])

(dlv/get-value db users-table "bar")
(dlv/get-value db users-table "foo")
(dlv/get-value db users-table "baz")

(dlv/get-range db users-table [:all])

(dlv/transact-kv db 
 [[:del users-table "foo"]])

(dlv/close-kv db)
