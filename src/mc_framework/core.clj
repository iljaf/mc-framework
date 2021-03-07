(ns mc-framework.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(defprotocol Product
  (payoff-function [product])
  (generate-payoff [product spot])
  (generate-payoff-pathwise [product paths]))

(defprotocol Model
  (generate-path [model product]))

(defprotocol RNG 
  (nextU [rng])
  (nextG [rng]))



(defrecord Eq-european-call [underlying strike expiry timeline sample-d]
  Product
  (payoff-function [_] (map #(max 0 (- % strike))))
  (generate-payoff [_ spot] (max 0 (- spot strike)))
  (generate-payoff-pathwise [product paths] (transduce (payoff-function product) conj paths)))

(defrecord Eq-barrier-call [underlying strike expiry barrier]
  Product
  (generate-payoff [_ spot] (if (< spot barrier) (max 0 (- spot strike) 0) 0)))

(defrecord Sample-Def [numeraire forward-mts discount-mts libor-def])
(defrecord Sample [numeraire forwards discounts libors])

(defn make-sample [^Sample-Def s-def]
  (let [numeraire 1
        f (vec (repeat (count (:forward-mts s-def)) 100))
        d (vec (repeat (count (:discount-mts s-def)) 1))
        l (vec (repeat (count (:libor-def s-def)) 0))]
    (->Sample numeraire f d l)))

;; TEST 
(def test-s-def  (->Sample-Def 1 [0.5 1] [0.5 1] [0.5 1]))
(make-sample test-s-def)

(def eq-european-1 (->Eq-european-call :aapl 100 0.5))
(def eq-barrier-1 (->Eq-barrier-call :aapl 90 0.5 110))

(def paths [100 150 230 95 20])
(generate-payoff-pathwise eq-european-1 paths)

(generate-payoff (->Eq-barrier-call :aapl 90 0.5 99) 93)

(comment 
  (generate-random-numbers ) ;; -> rng - generated by the RNG
  (generate-paths model rng) ;; -> paths - generated by the model
  (compute-payoff product paths) ;; produced by the product
  )

(generate-payoff eq-european-1 100)
(generate-payoff eq-barrier-1 100)


