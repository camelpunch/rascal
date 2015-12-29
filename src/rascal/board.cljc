(ns rascal.board)

(declare decrease increase)

(def . ".")
(def c "@")

(defn ->Board [width height]
  (vec (repeat height
               (vec (repeat width .)))))

(defn move-left
  [s]
  (update-in s [:player :coords :x] (decrease)))

(defn move-right
  [{board :board :as s}]
  (update-in s [:player :coords :x] (increase (-> board first count dec))))

(defn move-up
  [s]
  (update-in s [:player :coords :y] (decrease)))

(defn move-down
  [{board :board :as s}]
  (update-in s [:player :coords :y] (increase (-> board count dec))))

(defn render
  [{board :board {{x :x y :y} :coords} :player :as s}]
  (assoc-in board [y x] c))

(defn- decrease
  []
  (fn [n]
    (if (zero? n)
      n
      (dec n))))

(defn- increase
  [maximum]
  (fn [n]
    (if (= maximum n)
      n
      (inc n))))
