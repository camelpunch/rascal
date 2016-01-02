(ns rascal.pages
  (:require [hiccup.page :refer [html5 include-css include-js]]))

(defn index
  []
  (html5
   [:head
    (include-css "http://yui.yahooapis.com/3.18.1/build/cssreset/cssreset-min.css"
                 "http://yui.yahooapis.com/3.18.1/build/cssfonts/cssfonts-min.css"
                 "http://yui.yahooapis.com/3.18.1/build/cssgrids/cssgrids-min.css"
                 "style.css")]
   [:body
    [:div#app]
    (include-js "main.js")
    [:script "rascal.core.run();"]]))
