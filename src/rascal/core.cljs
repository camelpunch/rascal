(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.game :as g]
            [rascal.tiles :as t]
            [rascal.render :refer [render]]))

(enable-console-print!)

(def state (r/atom {:board     (t/make-board                 30 25)
                    :player    (t/make-player                15 23)
                    :obstacles (conj (t/make-walls-for-board 30 25)
                                     (t/make-creature \j "Jackal" 13 10)
                                     (t/make-creature \r "Rat"     1  1))
                    :log       ["Player enters the dungeon"]}))

(def keymap
  {72 #(g/move % t/x-axis dec)
   76 #(g/move % t/x-axis inc)
   75 #(g/move % t/y-axis dec)
   74 #(g/move % t/y-axis inc)})

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

(defn health-line
  [idx obstacle]
  [:li {:key (str "health" idx)}
   [:h2.bld "(" (:tile obstacle) ") " (:name obstacle)]
   [:p.break "Health: " (:health obstacle)]])

(defn log-line
  [idx msg]
  [:li {:key (str "log" idx)} msg])

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
     [:ul (map-indexed health-line (filter #(contains? % :health) (:obstacles @state)))]]
    [:div.board.yui3-u-3-4
     [:table.textC.break
      [:tbody (map-indexed game-row (render @state))]]
     [:h2.bld "Keys:"]
     [:p "h,j,k,l - movement"]
     [:h2.bld "Activity:"]
     [:ol (map-indexed log-line (:log @state))]]]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))
