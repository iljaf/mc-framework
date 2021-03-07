(ns mc-framework.core
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(defprotocol Payoff
  (payoff-function [product])
  (generate-payoff [product spot])
  (generate-payoff-pathwise [product paths]))


(defrecord Eq-european-call [underlying strike expiry]
  Payoff
  (payoff-function [product] (map #(max 0 (- % strike))))
  (generate-payoff [product spot] (max 0 (- spot strike)))
  (generate-payoff-pathwise [product paths] (transduce (payoff-function product) conj paths)))

(defrecord Eq-barrier-call [underlying strike expiry barrier]
  Payoff
  (generate-payoff [product spot] (if (< spot barrier) (max 0 (- spot strike) 0) 0)))

(def eq-european-1 (->Eq-european-call :aapl 100 0.5))
(def eq-barrier-1 (->Eq-barrier-call :aapl 90 0.5 110))

(def paths [100 150 230 95 20])
(generate-payoff-pathwise eq-european-1 paths)

(generate-payoff eq-european-1 100)
(generate-payoff eq-barrier-1 100)

(generate-payoff (->Eq-barrier-call :aapl 90 0.5 99) 93)

(comment 
  (init-simulation)
  (generate-paths)
  (compute-payoff))

