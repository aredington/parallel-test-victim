(ns parallel-test-victim.fixtures-test
  (:require [clojure.test :refer :all]
            [com.holychao.parallel-test :as ptest]))

(def ^:dynamic *bindable-var* nil)

(def category-index-tuples (atom []))

(defn bind-var
  [f]
  (binding [*bindable-var* true]
    (f)))

(defn log-tuple
  [f]
  (swap! category-index-tuples conj [ptest/*category* ptest/*index*])
  (f))

(use-fixtures :each bind-var)

(use-fixtures :once log-tuple)

(deftest ^:parallel var-is-bound-a
  (testing "*bindable-var* is bound in a parallel test."
    (is *bindable-var* "The fixture did not bind *bindable-var* to true.")))

(deftest ^:parallel var-is-bound-b
  (testing "*bindable-var* is bound in another parallel-test."
    (is *bindable-var* "The fixture did not bind *bindable-var* to true.")))

(defmacro spam-once-fixture-deftests
  "Generate a large volume of tests to parallelize over to try and
  provoke the once fixture being invoked more than once per
  thread/category combo."
  []
  `(do
     ~@(for [i (range
                (* 10
                   (.availableProcessors
                    (Runtime/getRuntime))))]
         `(deftest ~(with-meta (symbol (str "test-category-indices-" i))
                      {:parallel true})
            (testing "The once fixture is not called more than once per index/category combo."
              (is (= @category-index-tuples (distinct @category-index-tuples))))))))

(spam-once-fixture-deftests)
