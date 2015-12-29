(set-env! :dependencies '[[adzerk/boot-cljs "1.7.170-3" :scope "test"]
                          [adzerk/boot-reload "0.4.2" :scope "test"]
                          [adzerk/boot-test "1.1.0" :scope "test"]
                          [pandeiro/boot-http "0.7.1-SNAPSHOT" :scope "test"]
                          [org.clojure/tools.namespace "0.2.11" :scope "test"]
                          [org.clojure/clojurescript "1.7.170"]
                          [reagent "0.6.0-alpha"]
                          [bidi "1.24.0"]
                          [hiccup "1.0.5"]]
          :source-paths #{"src"}
          :resource-paths #{"html" "css"})

(require '[adzerk.boot-cljs :refer :all]
         '[adzerk.boot-reload :refer [reload]]
         '[pandeiro.boot-http :refer [serve]]
         '[adzerk.boot-test :refer :all])

(deftask testing
  []
  (set-env! :source-paths #(conj % "test"))
  identity)

(deftask build
  []
  (comp (cljs :source-map true) (target)))

(deftask run
  []
  (comp (serve :handler 'rascal.server/app :reload true)
        (watch)
        (reload :on-jsload 'rascal.core/run)
        (build)))
