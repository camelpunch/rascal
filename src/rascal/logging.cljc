(ns rascal.logging)

(defn log
  [xs & ys]
  (conj xs (apply str ys)))
