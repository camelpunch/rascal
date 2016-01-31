(ns rascal.game
  (:require [rascal.logging :refer [log]]
            [rascal.tiles :as t]
            [rascal.battle :refer [do-battle]]))

(defn make-game
  [& {player-coords    :player
      board-dimensions :board
      monsters         :monsters
      dice-rolls       :dice-rolls}]
  (let [[rolled to-be-rolled] (split-at (* 2 (count monsters)) dice-rolls)]
    {:turn       1
     :player     (apply t/make-player player-coords)
     :board      (apply t/make-board board-dimensions)
     :obstacles  (concat (apply t/make-walls-for-board board-dimensions)
                         (t/place-creatures :board-dimensions board-dimensions
                                            :dice-rolls       rolled
                                            :creatures        monsters))
     :dice-rolls to-be-rolled
     :log        ["You entered the dungeon"]}))

(defn- extra-log-messages
  [state]
  (if (t/dead? (:player state))
    (update-in state [:log] log "You die")
    state))

(defn move
  [old-state f]
  (if (t/dead? (:player old-state))
    old-state
    (-> (do-battle old-state (f old-state))
        extra-log-messages
        (update-in [:turn] inc))))

(def left  #(update-in % t/x-axis dec))
(def right #(update-in % t/x-axis inc))
(def up    #(update-in % t/y-axis dec))
(def down  #(update-in % t/y-axis inc))
