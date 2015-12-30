(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.board :as b :refer [->Board]]))

(enable-console-print!)

(def new-jackal
  {:tile   \j
   :name   "Jackal"
   :health 100})

(def new-kobold
  {:tile   \k
   :name   "Kobold"
   :health 100})

(defn at
  [tile [x y]]
  (assoc-in tile [:coords] {:x x :y y}))

(def state (r/atom {:board (->Board 15 15)
                    :player {:tile   \@
                             :coords {:x 7 :y 7}}
                    :monsters [(-> new-jackal (at [13 10]))
                               (-> new-kobold (at [ 1  1]))]}))

(def keymap
  {72 b/move-left
   76 b/move-right
   75 b/move-up
   74 b/move-down})

(defn keydown-handler
  [e]
  (when-let [f (keymap (-> e .-keyCode))]
    (swap! state f)))

(defn game-cell
  [idx contents]
  [:td.cell {:key (str "td" idx)} contents])

(defn game-row
  [idx contents]
  [:tr {:key (str "tr" idx)}
   (map-indexed game-cell contents)])

(defn main
  []
  [:div#game.invisibleFocus.textC
   {:tab-index 0
    :on-key-down keydown-handler}
   [:h1 "Rascal"]
   [:table.marginC
    [:tbody (map-indexed game-row (b/render @state))]]
   [:ul
    (for [monster (:monsters @state)]
      [:li.item1of3
       [:h2 (:name monster)]
       [:p "Health: " (:health monster)]])]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))
