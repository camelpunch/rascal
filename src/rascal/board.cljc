(ns rascal.board
  (:require [rascal.render :refer [render]]))

(declare affect alive? move x-axis y-axis)

(defn make-wall-tile
  [x y]
  {:tile \#
   :name "Wall"
   :coords {:x x :y y}})

(defn make-empty-space
  [x y]
  {:tile \.
   :name "Empty space"
   :coords {:x x :y y}})

(defn horz-wall
  [width y]
  [(map #(make-wall-tile % y) (range width))])

(defn make-board
  [width height]
  (concat
   (horz-wall width 0)
   (for [y (range 1 (dec height))]
     (concat
      [(make-wall-tile 0 y)]
      (for [x (range 1 (dec width))]
        (make-empty-space x y))
      [(make-wall-tile (dec width) y)]))
   (horz-wall width (dec height))))

(defn make-creature
  [tile creature-name x y]
  {:tile   tile
   :name   creature-name
   :health 100
   :coords {:x x :y y}})

(defn make-player
  [x y]
  (make-creature \@ "Player" x y))

(defn damager
  [coords]
  (fn [monster]
    (if (= coords (:coords monster))
      (affect monster [:health] - 50)
      monster)))

(defn do-battle
  [{monsters :monsters :as s} f]
  (assoc s :monsters (filter alive? (map f monsters))))

(defn move-left [s] (move s x-axis dec))
(defn move-right [s] (move s x-axis inc))
(defn move-up [s] (move s y-axis dec))
(defn move-down [s] (move s y-axis inc))

(defn- wall? [x] (= \# (:tile x)))

(defn- move
  [s axis movement]
  (let [{{coords :coords} :player
         monsters         :monsters
         board            :board
         :as candidate-state} (update-in s axis movement)
        walls             (filter wall? (flatten board))
        obstacles         (concat monsters walls)]
    (if-let [battle-coords (some #{coords} (map :coords obstacles))]
      (do-battle s (damager battle-coords))
      candidate-state)))

(defn- affect
  [x ks f & args]
  (update-in x ks #(apply f % args)))

(def ^:private alive? (comp pos? :health))
(def ^:private x-axis [:player :coords :x])
(def ^:private y-axis [:player :coords :y])
