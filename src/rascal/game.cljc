(ns rascal.game
  (:require [rascal.logging :refer [log]]
            [rascal.tiles :as t]
            [rascal.monster-movement :as mm]
            [rascal.battle :as b]))

(defn make-game
  [& {player           :player
      board-dimensions :board
      monsters         :monsters
      dice-rolls       :dice-rolls}]
  (let [[coord-rolls to-be-rolled] (split-at (* 2 (count monsters)) dice-rolls)]
    {:turn       1
     :player     player
     :board      (apply t/make-board board-dimensions)
     :obstacles  (concat (apply t/make-walls-for-board board-dimensions)
                         (t/place-creatures :board-dimensions board-dimensions
                                            :dice-rolls       coord-rolls
                                            :creatures        monsters))
     :dice-rolls to-be-rolled
     :log        ["You entered the dungeon"]}))

(defn- game-over? [state] (-> state :player t/dead?))

(defn- extra-log-messages
  [state]
  (if (game-over? state)
    (update-in state [:log] log "You die")
    state))

(defn move
  "The main integrating function for the whole game"
  [state f]
  (if (game-over? state)
    state
    (let [new-state (mm/move-monsters (f state))]
      (-> state
          (b/do-battle new-state)
          extra-log-messages
          (update-in [:turn] inc)))))

(def left  #(update-in % [:player] t/move t/x-axis dec))
(def right #(update-in % [:player] t/move t/x-axis inc))
(def up    #(update-in % [:player] t/move t/y-axis dec))
(def down  #(update-in % [:player] t/move t/y-axis inc))
(def up-left    (comp up left))
(def up-right   (comp up right))
(def down-left  (comp down left))
(def down-right (comp down right))
