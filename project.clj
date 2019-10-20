(defproject tabellinator "0.1.2"
  :description "Quante belle tabelline"
  :url "https://github.com/l3nz/tabellinator"
  :license {:name "MIT"}
  :main tabellinator.core

  :aliases {"fix" ["cljfmt" "fix"]
            ; Kondo
            "clj-kondo" ["with-profile" "kondo"
                         "trampoline" "run" "-m"
                         "clj-kondo.main" "--" "--lint" "src/" "--cache" ".cli-kondo-cache"]
            "clj-kondo-test" ["with-profile" "kondo"
                              "trampoline" "run" "-m"
                              "clj-kondo.main" "--" "--lint" "test/" "--cache" ".cli-kondo-cache"]}

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/spec.alpha "0.1.143"]
                 [hiccup "1.0.5"]
                 [cli-matic "0.3.8"]
                 [say-cheez "0.1.1"]]

  :plugins [[lein-eftest "0.5.1"]
            [jonase/eastwood "0.2.5"]
            [lein-kibit "0.1.6"]
            [lein-cljfmt "0.6.4"]
            [lein-cljsbuild "1.1.7"]
            [lein-doo "0.1.11"]
            [lein-ancient "0.6.15"]]

  :profiles {:kondo
             {:dependencies
              [[org.clojure/clojure "1.10.1"]
               [clj-kondo "2019.10.11-alpha"]]}})
