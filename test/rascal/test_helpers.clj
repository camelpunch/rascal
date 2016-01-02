(ns rascal.test-helpers)

(defn rendered
  [b]
  (map vec (clojure.string/split b #"\n +")))
