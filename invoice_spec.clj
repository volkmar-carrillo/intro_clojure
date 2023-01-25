(ns invoice-spec
  (:require
    [clojure.spec.alpha :as s] ))

(s/def :customer/name string?)
(s/def :customer/email string?)
(s/def :invoice/customer (s/keys :req-un [:customer/name
                                       :customer/email]))

;(s/def :tax/rate double?)
(s/def :tax/tax_rate int?)
;(s/def :tax/category #{:iva})
(s/def :tax/tax_category #{"IVA"})
(s/def ::tax (s/keys :req-un [:tax/tax_category
                           :tax/tax_rate]))
(s/def :invoice-item/taxes (s/coll-of ::tax :kind vector? :min-count 1))

(s/def :invoice-item/price double?)
(s/def :invoice-item/quantity double?)
(s/def :invoice-item/sku string?)

(s/def ::invoice-item
  (s/keys :req-un [:invoice-item/price
                :invoice-item/quantity
                :invoice-item/sku
                :invoice-item/taxes]))

;(s/def :invoice/issue-date inst?)
(s/def :invoice/issue_date inst?)
(s/def :invoice/items (s/coll-of ::invoice-item :kind vector? :min-count 1))

(s/def ::invoice
  (s/keys :req-un [:invoice/issue_date
                :invoice/customer
                :invoice/items]))