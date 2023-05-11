(defproject cetus "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 ;;Server + routing
                 [ring/ring-jetty-adapter "1.7.1"]
                 [metosin/reitit "0.7.0-alpha3"]
                 [metosin/ring-swagger-ui "5.0.0-alpha.0"]
                 ;;Database
                 [datalevin "0.8.16"]]
  :main ^:skip-aot cetus.core
  :target-path "target/%s"
  :repl-options {:init-ns cetus.server}
  :jvm-opts ["--add-opens=java.base/java.nio=ALL-UNNAMED"
             "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED"]
  :profiles {:dev {:dependencies [[ring/ring-mock "0.3.2"]]}
             :uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
