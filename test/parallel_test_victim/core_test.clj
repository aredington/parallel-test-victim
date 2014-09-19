(ns parallel-test-victim.core-test
  (:require [clojure.test :refer :all]))

(def cycles 1000000)

(def point (atom nil))

(deftest core.async-injection-test
  (testing
      (is (nil? (require 'clojure.core.async))
          "Testing clojure.core.async is injected into project.clj")))

(deftest ^:parallel a-test
  (testing "atom modification (ascending)"
    (reset! point 0)
    (dotimes [i cycles]
      (swap! point inc))
    (is (not= @point cycles)
        "There was no concurrent access detected after 1 million iterations (ascending).")))

(deftest ^:parallel b-test
  (testing "atom modification (descending)"
    (reset! point cycles)
    (dotimes [i cycles]
      (swap! point dec))
    (is (not= @point 0)
        "There was no concurrent access detected after 1 million iterations (descending).")))
