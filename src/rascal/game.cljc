(ns rascal.game
  (:require [rascal.tiles :as t]))

(defn- same-coords?
  [x y]
  (= (:coords x) (:coords y)))

(defn- hit-anything?
  [x ys]
  (some #{(:coords x)} (map :coords ys)))

(defn- do-battle
  [old-state player new-obstacles]
  (reduce (fn [{log           :log
                acc-obstacles :obstacles
                :as acc}
               old-obstacle]
            (let [hit?         (same-coords? player old-obstacle)
                  new-obstacle (if hit? (t/damage old-obstacle) old-obstacle)]
              (assoc acc
                     :obstacles (if (t/dead? new-obstacle)
                                  acc-obstacles
                                  (conj acc-obstacles new-obstacle))
                     :log       (cond
                                  hit?  (conj log (str "You hit the " (:name new-obstacle)))
                                  :else log))))
          (assoc old-state :obstacles [])
          new-obstacles))

(defn move
  [old-state axis movement]
  (let [{player      :player
         c-obstacles :obstacles
         :as candidate-state} (update-in old-state axis movement)]
    (if (hit-anything? player c-obstacles)
      (do-battle old-state player c-obstacles)
      candidate-state)))

