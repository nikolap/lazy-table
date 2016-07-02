(defproject lazy-table "0.1.0"
  :description "Lazily render reagent tables"
  :license {:name "The MIT License (MIT)"
            :url  "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [reagent "0.6.0-rc"]]
  :profiles {:dev
             {:dependencies [[org.clojure/clojurescript "1.9.89" :scope "provided"]]
              :plugins      [[lein-cljsbuild "1.1.3"]]}})
