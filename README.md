# **Prueba Técnica - Clojure**

## **Problema 1 - Thread Last Operator**

Dada la factura definida en **invoice.edn**, utiliza el operador **thread-last ->>** para encontrar todos los elementos de la factura que satisfagan las condiciones dadas. 

Escriba una función que reciba **invoice** como argumento y devuelva todos los elementos que satisfagan las condiciones descritas a continuación.

### **Requerimientos**

Cargar la factura de esta manera para poder usar **invoice.edn**:

```clj
(def invoice (clojure.edn/read-string (slurp "invoice.edn")))
```

### **Definiciones**

- Un **invoice-item** es un ***mapa clojure*** { ... } que tiene un campo **:invoice-item/id**.

```clj
{:invoice-item/id "ii2"  
 :invoice-item/sku "SKU 2"}
```
- Una **invoice** tiene dos campos **:invoice/id *(su identificador)*** y **:invoice/items *un vector de elementos de factura***

### **Condiciones de invoice-item**

- Al menos un **invoice-item** tiene ***IVA 19***
- Al menos un **invoice-item** tiene ***retention :ret_fuente 1%***

- Cada artículo debe cumplir **EXACTAMENTE** una de las dos condiciones anteriores. Esto significa que un **invoice-item** **no puede tener TANTO IVA 19 como retention :ret_fuente 1%.**

---

## **SOLUCIÓN**

### **Nombre del Espacio de Trabajo**

```clj
(ns thread_last_operator)
```

### **Lectura del Archivo invoice.edn**

```clj
(def invoice (clojure.edn/read-string (slurp "invoice.edn")))
=>
#:invoice{:id "i1",
          :items [{:invoice-item/id "ii1",
                   :invoice-item/sku "SKU 1",
                   :taxable/taxes [#:tax{:id "t1", :category :iva, :rate 19}],
                   :retentionable/retentions [#:retention{:id "r1", :category :ret_fuente, :rate 1}]}
                  {:invoice-item/id "ii2",
                   :invoice-item/sku "SKU 2",
                   :taxable/taxes [#:tax{:id "t2", :category :iva, :rate 16}]}
                  {:invoice-item/id "ii3",
                   :invoice-item/sku "SKU 3",
                   :taxable/taxes [#:tax{:id "t3", :category :iva, :rate 19}]}
                  {:invoice-item/id "ii3",
                   :invoice-item/sku "SKU 3",
                   :retentionable/retentions [#:retention{:id "r2", :category :ret_fuente, :rate 1}]}
                  {:invoice-item/id "ii4",
                   :invoice-item/sku "SKU 4",
                   :retentionable/retentions [#:retention{:id "r3", :category :ret_fuente, :rate 2}]}]}

```

### **Validador de invoice-item dadas las condiciones iniciales**

- Al menos un **invoice-item** tiene ***IVA 19***
- Al menos un **invoice-item** tiene ***retention :ret_fuente 1%***

- Cada artículo debe cumplir EXACTAMENTE una de las dos condiciones anteriores. Esto significa que un **invoice-item** **no puede tener TANTO IVA 19 como retention :ret_fuente 1%.**

```clj
(defn
  valid_item? [{taxes :taxable/taxes
                retentions :retentionable/retentions}]

  (let [valid_retention_rate?  (->> retentions (some #(= (:retention/rate %) 1)))
        valid_tax_rate? (->> taxes (some #(= (:tax/rate %) 19)))]
    (if (and valid_retention_rate? valid_tax_rate?)
      false
      (or valid_retention_rate? valid_tax_rate?))))
```

### **Ejecución de la Función Principal**

```clj
(defn init
  [invoice]
  (->> (get invoice :invoice/items)
       (filter valid_item?)))

=>
({:invoice-item/id "ii3", :invoice-item/sku "SKU 3", :taxable/taxes [#:tax{:id "t3", :category :iva, :rate 19}]}
 {:invoice-item/id "ii3",
  :invoice-item/sku "SKU 3",
  :retentionable/retentions [#:retention{:id "r2", :category :ret_fuente, :rate 1}]})

```

---

## **Problema 2 - Core Generating Functions**

Dada la factura definida en **invoice.json**, generar una factura que pase la especificación **::invoice** definida en **invoice-spec.clj**. 

Escribe una función que como argumento reciba un nombre de fichero **(un nombre de fichero JSON en este caso)** y devuelva un **mapa clojure** tal que

```clj
(s/valid? ::invoice invoice)

=> 
true 
```
donde **invoice** representa una factura construida a partir del **JSON**.

---

## **SOLUCIÓN**

### **Nombre del Espacio de Trabajo y Requerimientos**

```clj
(ns core_generating_functions
  (:require
    [clojure.data.json :as json]
    [clojure.spec.alpha :as s]
    [invoice-spec :as invoice]
    [clj-time.format :as f]))
```

### **Formato Fecha del JSON**

Dado que para enviar datos tipo fecha a validaciones tipo **Clojure.spec**, se deben procesar primero para que este reconozca que sea tipo **fecha (inst?)**

```clj
(def custom_formatter (f/formatter "dd/MM/yyyy"))
```

### **Validar Clave Fecha de invoice.json**
Esta función recibe una Clave y un Valor, siendo esto una buena manera para encontrar **:issue_date** y asi parsear la fecha.

```clj
(defn valid_date? [key value]
  (if (= key :issue_date)
    (f/parse custom_formatter value)
    value))
```

### **Transformación de JSON a un Mapa Clojure**

Dado que nuestro archivo JSON solo contiene una clave **:invoice** debemos especificarlo al leerlo, de esta manera, permitirá a **:key-fn** encontrar las claves que contiene **:invoice**

Además, con **:value-fn** podemos aprovechar la función que permite procesar la fecha y de esta manera validar correctamente nuestro **JSON** con nuestra estructura **SPEC**

```clj
(defn invoice
  [name_json]
  (:invoice (json/read (clojure.java.io/reader name_json)
                       :value-fn valid_date?
                       :key-fn keyword)))
```

### **Validación de la Estructura JSON mediante la Estructura Spec**

```clj
(s/valid? ::invoice/invoice (invoice "invoice.json"))

(s/explain ::invoice/invoice (invoice "invoice.json"))

=> true
Success!
=> nil
```

---

## **Problema 3 - Test Driven Development**

Dada la función **subtotal** definida en **invoice-item.clj**, escribe al menos cinco pruebas utilizando el **deftest** del **core de clojure** que demuestren su **corrección**. 

Esta función subtotal calcula el subtotal de un elemento de factura teniendo en cuenta un tipo de descuento. ***Asegúrate de que las pruebas cubren tantos casos extremos como puedas.***

---

## **SOLUCIÓN**
### **Nombre del Espacio de Trabajo y Requerimientos**

```clj
(ns testing
  (:require
    [invoice-item :as invoice-item]))

(use 'clojure.test)
```

### ***Casos de Prueba Básicos***

```clj
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
```

### ***Casos de Prueba Complejos***

```clj
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
```

### **Ejecución de las Pruebas**

```clj
(run-tests 'testing)

Testing testing

FAIL in (complex_testing) (form-init15827382535684838191.clj:39)
Calculando Subtotal con Cantidad Negativa
expected: (= (invoice-item/subtotal item) 0.0)
  actual: (not (= -9.0 0.0))

FAIL in (complex_testing) (form-init15827382535684838191.clj:43)
Calculando Subtotal con Precio Negativo
expected: (= (invoice-item/subtotal item) 0.0)
  actual: (not (= -9.0 0.0))

FAIL in (complex_testing) (form-init15827382535684838191.clj:47)
Calculando Subtotal con Descuento Superior al 100%
expected: (= (invoice-item/subtotal item) 0.0)
  actual: (not (= -5.0 0.0))

Ran 2 tests containing 10 assertions.
3 failures, 0 errors.
=> {:test 2, :pass 7, :fail 3, :error 0, :type :summary}
```

Con estos resultados concluimos que una buena corrección para la función **subtotal** sería la implementación de **exceptions** para controlar sus tipos de datos y cantidades permitidas de los valores.



**By**<br>
**Volkmar Carrillo**<br>
**carrillo.ramklov@gmail.com** 