(ns rascal.board)

(declare add-tiles max-x max-y toward x-axis y-axis)

(defn ->Board [width height]
  (vec (repeat height
               (vec (repeat width \.)))))

(defn move-left
  [s]
  (update-in s x-axis (toward 0 dec)))

(defn move-right
  [{board :board :as s}]
  (update-in s x-axis (toward (max-x board) inc)))

(defn move-up
  [s]
  (update-in s y-axis (toward 0 dec)))

(defn move-down
  [{board :board :as s}]
  (update-in s y-axis (toward (max-y board) inc)))

(defn render
  [{board    :board
    player   :player
    monsters :monsters}]
  (add-tiles board (conj monsters player)))

(defn- add-tiles
  [board tiles]
  (reduce (fn [brd
               {{x :x y :y} :coords
                tile        :tile}]
            (assoc-in brd [y x] tile))
          board
          tiles))

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
