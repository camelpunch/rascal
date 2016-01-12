(ns rascal.core
  (:require [reagent.core :as r]
            [rascal.game :as g :refer [move left right up down]]
            [rascal.tiles :as t]
            [rascal.render :refer [render]]))

(enable-console-print!)

(def state (r/atom (g/make-game
                    :board      [30 25]
                    :player     [15 23]
                    :monsters   [[\j "Jackal"]
                                 [\r "Rat"]
                                 [\p "Pheasant"]]
                    :dice-rolls (repeatedly #(rand-int 10)))))

(def keymap
  {72 #(move % left)
   76 #(move % right)
   75 #(move % up)
   74 #(move % down)
   89 #(move % (comp left up))
   85 #(move % (comp right up))
   78 #(move % (comp left down))
   77 #(move % (comp right down))})

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
     [:p "Health: " (get-in @state [:player :health])]
     [:p "Turn: " (:turn @state)]]
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
     [:ol (map-indexed log-line (reverse (:log @state)))]]]])

(def main-focused
  (-> main (with-meta {:component-did-mount
                       (fn [this]
                         (.focus (js/document.getElementById "game")))})))

(defn ^:export run
  []
  (r/render [main-focused] (js/document.getElementById "app")))
