(ns testing
  (:require
    [invoice-item :as invoice-item]))

(use 'clojure.test)

(deftest basic_testing
  (testing "Calculando Subtotal sin Descuento"
    (let [item {:invoice-item/precise-quantity 2 :invoice-item/precise-price 5.0}]
      (is (= (invoice-item/subtotal item) 10.0))))

  (testing "Calculando Subtotal con el 10% de Descuento"
    (let [item {:invoice-item/precise-quantity 2 :invoice-item/precise-price 5.0 :invoice-item/discount-rate 10}]
      (is (= (invoice-item/subtotal item) 9.0))))

  (testing "Calculando Subtotal con el 25% de Descuento"
    (let [item {:invoice-item/precise-quantity 3 :invoice-item/precise-price 10.0 :invoice-item/discount-rate 25}]
      (is (= (invoice-item/subtotal item) 22.5))))

  (testing "Calculando Subtotal con el 50% de Descuento"
    (let [item {:invoice-item/precise-quantity 5 :invoice-item/precise-price 20.0 :invoice-item/discount-rate 50}]
      (is (= (invoice-item/subtotal item) 50.0))))

  (testing "Calculando Subtotal con el 100% de Descuento"
    (let [item {:invoice-item/precise-quantity 2 :invoice-item/precise-price 30.0 :invoice-item/discount-rate 100}]
      (is (= (invoice-item/subtotal item) 0.0)))))

(deftest complex_testing
  (testing "Calculando Subtotal con Cantidad 0"
    (let [item {:invoice-item/precise-quantity 0 :invoice-item/precise-price 5.0 :invoice-item/discount-rate 10}]
      (is (= (invoice-item/subtotal item) 0.0))))

  (testing "Calculando Subtotal con Precio 0"
    (let [item {:invoice-item/precise-quantity 2 :invoice-item/precise-price 0 :invoice-item/discount-rate 10}]
      (is (= (invoice-item/subtotal item) 0.0))))

  (testing "Calculando Subtotal con Cantidad Negativa"
    (let [item {:invoice-item/precise-quantity -2 :invoice-item/precise-price 5.0 :invoice-item/discount-rate 10}]
      (is (= (invoice-item/subtotal item) 0.0))))

  (testing "Calculando Subtotal con Precio Negativo"
    (let [item {:invoice-item/precise-quantity 2 :invoice-item/precise-price -5.0 :invoice-item/discount-rate 10}]
      (is (= (invoice-item/subtotal item) 0.0))))

  (testing "Calculando Subtotal con Descuento Superior al 100%"
    (let [item {:invoice-item/precise-quantity 2 :invoice-item/precise-price 5.0 :invoice-item/discount-rate 150}]
      (is (= (invoice-item/subtotal item) 0.0)))))

(run-tests 'testing)