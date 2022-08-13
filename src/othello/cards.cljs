(ns ^:figwheel-hooks othello.cards
  (:require [devcards.core]
            [othello.view]))

(enable-console-print!)

(defn render []
  (devcards.core/start-devcard-ui!))

(defn ^:after-load render-on-reload []
  (render))

(render)
