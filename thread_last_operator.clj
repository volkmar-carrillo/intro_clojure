(ns thread_last_operator)

(def invoice (clojure.edn/read-string (slurp "invoice.edn")))

(defn
  valid_item? [{taxes :taxable/taxes
                retentions :retentionable/retentions}]

  (let [valid_retention_rate?  (->> retentions (some #(= (:retention/rate %) 1)))
        valid_tax_rate? (->> taxes (some #(= (:tax/rate %) 19)))]
    (if (and valid_retention_rate? valid_tax_rate?)
      false
      (or valid_retention_rate? valid_tax_rate?))))

(defn init
  [invoice]
  (->> (get invoice :invoice/items)
       (filter valid_item?)))