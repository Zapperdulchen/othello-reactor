(ns ^:figwheel-hooks othello.cards
  (:require [devcards.core]
            [cljs.test :refer-macros [run-all-tests]]
            [othello.view]
            [othello.reactor-core-test]
            [othello.reactor-test]
            ;; I do not understand why I need to require the following, but it
            ;; stopped working without
            [othello.othello]
            [othello.tictactoe]
            [othello.reactor]))

(enable-console-print!)

(defmethod cljs.test/report [:cljs.test/default :end-run-tests] [m]
  (if (cljs.test/successful? m)
    (println "Success!")
    (println "FAIL")))

(defn render []
  (devcards.core/start-devcard-ui!))

(defn ^:after-load render-on-reload []
  (render))

(println "Inside othello.cards ")
(render)
(run-all-tests)
