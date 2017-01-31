(ns dspec.helpers-test
  #?(:cljs (:require-macros [cljs.core :refer [doseq]]
                            [cljs.spec.test :as stest]))
  (:require [clojure.test :refer [deftest is]]
            #?(:clj [clojure.spec.test :as stest]
               :cljs cljs.spec.test)
            [lab79.dspec.helpers :refer [specs->datomic create-clojure-specs!]]
            #?(:clj  [clojure.spec.gen :as gen]
               :cljs [cljs.spec.impl.gen :as gen])
            #?(:clj  [datomic.api :as d]
               :cljs [datascript.core :as d])
            [dspec.util :refer [db-id? instrument-all!]]))

#?(:cljs (enable-console-print!))

; Instrument all our functions in dspec
(instrument-all!)

(let [specs [#:interface.def{:name :interface/helper-a
                             :fields {:helper-a/key [:keyword :gen/should-generate]}
                             :identify-via [['?e :helper-a/key]]}]]
  (deftest test-specs->datomic
    (let [{:datomic/keys [partition-schema enum-schema field-schema]} (specs->datomic specs d/tempid)]
      (is (empty? partition-schema))
      (is (empty? enum-schema))
      (is (= #{{:db/ident :helper-a/key
                :db/valueType :db.type/keyword
                :db/cardinality :db.cardinality/one
                :db.install/_attribute :db.part/db}}
             (set (map #(dissoc % :db/id) field-schema))))))

  (deftest test-create-clojure-specs!
    ; should not throw
    (create-clojure-specs! specs d/tempid db-id?)))