(ns core_generating_functions
  (:require
    [clojure.data.json :as json]
    [clojure.spec.alpha :as s]
    [invoice-spec :as invoice]
    [clj-time.format :as f]))

(def custom_formatter (f/formatter "dd/MM/yyyy"))

(defn valid_date? [key value]
  (if (= key :issue_date)
    (f/parse custom_formatter value)
    value))

(defn invoice
  [name_json]
  (:invoice (json/read (clojure.java.io/reader name_json)
                       :value-fn valid_date?
                       :key-fn keyword)))

(s/valid? ::invoice/invoice (invoice "invoice.json"))

(s/explain ::invoice/invoice (invoice "invoice.json"))