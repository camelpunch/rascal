(ns rascal.monster-movement
  (:require [rascal.tiles :as t]))

(defn- towards
  [dst]
  (fn [x] (if (< x dst) (inc x) (dec x))))

(defn move-monsters
  [{{{player-x :x
      player-y :y} :coords} :player
    :as state}]
  (update-in state [:obstacles]
             #(map (fn [obstacle]
                     (-> obstacle
                         (t/move t/x-axis (towards player-x))
                         (t/move t/y-axis (towards player-y))))
                   %)))
