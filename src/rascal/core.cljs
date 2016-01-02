(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.game :as g]
            [rascal.tiles :as t]
            [rascal.render :refer [render]]))

(enable-console-print!)

(def state (r/atom {:board    (t/make-board                 15 15)
                    :player   (t/make-player                 7  7)
                    :monsters [(t/make-creature \j "Jackal" 13 10)
                               (t/make-creature \r "Rat"     1  1)]}))

(def keymap
  {72 g/move-left
   76 g/move-right
   75 g/move-up
   74 g/move-down})

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
   [:p [:a {:href "https://github.com/camelpunch/rascal"} "Source"]]
   [:table.marginC
    [:tbody (map-indexed game-row (render @state))]]
   [:ul
    (for [monster (:monsters @state)]
      [:li.item1of3 {:key (str monster)}
       [:h2 (:name monster)]
       [:p "Health: " (:health monster)]])]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))
