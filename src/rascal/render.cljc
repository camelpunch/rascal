(ns rascal.render)

(def render-row (comp vec #(map :tile %)))

(defn- render-board
  [logical-board]
  (vec (map render-row logical-board)))

(defn- add-tiles
  [board tiles]
  (reduce (fn [b
               {{x :x y :y} :coords
                tile        :tile}]
            (assoc-in b [y x] tile))
          board
          tiles))

(defn render
  [{board    :board
    player   :player
    monsters :monsters}]
  (add-tiles (render-board board) (conj monsters player)))
