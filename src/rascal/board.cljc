(ns rascal.board)

(declare add-tiles affect alive? move
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

(defn damager
  [coords]
  (fn [monster]
    (if (= coords (:coords monster))
      (affect monster [:health] - 50)
      monster)))

(defn do-battle
  [{monsters :monsters :as s} f]
  (assoc s :monsters (filter alive? (map f monsters))))

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
  (let [{{coords :coords} :player
         monsters         :monsters
         :as candidate-state }
        (update-in s axis movement)]
    (if-let [battle-coords (some #{coords} (map :coords monsters))]
      (do-battle s (damager battle-coords))
      candidate-state)))

(defn- add-tiles
  [board tiles]
  (reduce (fn [brd
               {{x :x y :y} :coords
                tile        :tile}]
            (assoc-in brd [y x] tile))
          board
          tiles))

(defn- alive?
  [x]
  (pos? (:health x)))

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
