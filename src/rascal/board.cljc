(ns rascal.board
(:require [clojure.set :refer [intersection]]))

(declare add-tiles affect move
         max-x max-y toward x-axis y-axis)

(defn make-board
  [width height]
  (vec (repeat height
               (vec (repeat width \.)))))

(defn make-creature
  [tile s x y]
  (-> {:tile   tile
       :name   s
       :health 100
       :coords {:x x :y y}}))

(defn make-player
  [x y]
  (make-creature \@ "Player" x y))

(defn render
  [{board    :board
    player   :player
    monsters :monsters}]
  (add-tiles board (conj monsters player)))

(defn do-battle
  [{monsters :monsters :as s} enemy-coords]
  (assoc s :monsters (map (fn [monster]
                            (if (= enemy-coords (:coords monster))
                              (affect monster [:health] - 50)
                              monster))
                          monsters)))

(defn move-left
  [s]
  (move s x-axis (toward 0 dec))) ; TODO: make walls as tiles, remove need for 0 checking

(defn move-right
  [{board :board :as s}]
  (move s x-axis (toward (max-x board) inc)))

(defn move-up
  [s]
  (move s y-axis (toward 0 dec)))

(defn move-down
  [{board :board :as s}]
  (move s y-axis (toward (max-y board) inc)))

(defn- move
  [s axis movement]
  (let [{player   :player
         monsters :monsters
         :as candidate-state} (update-in s axis movement)
        player-coords         (set [(:coords player)])
        monsters-coords       (set (map :coords monsters))
        intersections         (intersection player-coords monsters-coords)]
    (if (empty? intersections)
      candidate-state
      (do-battle s (first intersections)))))

(defn- add-tiles
  [board tiles]
  (reduce (fn [brd
               {{x :x y :y} :coords
                tile        :tile}]
            (assoc-in brd [y x] tile))
          board
          tiles))

(defn- affect
  [x ks f & args]
  (update-in x ks #(apply f % args)))

(defn- toward
  [x f]
  #(if (= x %)
     %
     (f %)))

(def ^:private x-axis [:player :coords :x])
(def ^:private y-axis [:player :coords :y])
(def ^:private last-index (comp dec count))
(def ^:private max-y last-index)
(def ^:private max-x (comp last-index first))
