(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.game :as g]
            [rascal.tiles :as t]
            [rascal.render :refer [render]]))

(enable-console-print!)

(def state (r/atom {:board    (t/make-board                 30 25)
                    :player   (t/make-player                15 23)
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
  [:div#game.page.invisibleFocus
   {:tab-index 0
    :on-key-down keydown-handler}
   [:div.break.yui3-g
    [:div.yui3-u-1-3
     [:h2.bld "Player"]
     [:p "Health: " (get-in @state [:player :health])]]
    [:div.yui3-u-1-3.textC
     [:h1.mainHeading "Rascal"]
     [:p [:a {:href "https://github.com/camelpunch/rascal"} "Source"]]]
    [:div.yui3-u-1-3]]
   [:div.yu3-g
    [:div.yui3-u-1-4
     [:ul.monsters
     (for [monster (:monsters @state)]
       [:li.monster {:key (str monster)}
        [:h2.bld (:name monster)]
        [:p.monster-detail.break "Health: " (:health monster)]])]]
    [:div.board.yui3-u-3-4
     [:table.textC.break
      [:tbody (map-indexed game-row (render @state))]]
     [:h2.bld "Keys:"]
     [:p "h,j,k,l - movement"]]]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))
