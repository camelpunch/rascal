(ns rascal.game
  (:require [rascal.logging :refer [log]]
            [rascal.tiles :as t]
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
  [state f]
  (if (game-over? state)
    state
    (let [new-state (f state)]
      (-> (b/do-battle state new-state)
          extra-log-messages
          (update-in [:turn] inc)))))

(def left  #(update-in % t/x-axis dec))
(def right #(update-in % t/x-axis inc))
(def up    #(update-in % t/y-axis dec))
(def down  #(update-in % t/y-axis inc))
