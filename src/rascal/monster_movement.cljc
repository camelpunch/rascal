(ns rascal.monster-movement
  (:require [rascal.tiles :as t]))

(defn- towards
  [dst]
  (fn [x] (if (< x dst) (inc x) (dec x))))

(defn move-monsters
  [{{{player-x :x} :coords} :player
    :as state}]
  (update-in state [:obstacles]
             #(map (fn [obstacle]
                     (t/move obstacle t/x-axis (towards player-x)))
                   %)))
