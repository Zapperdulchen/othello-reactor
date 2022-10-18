(ns ^:figwheel-always othello.core
  (:require [othello.game :as game]
            [othello.tictactoe :as tictactoe]
            [othello.othello :as othello]
            [othello.reactor :as reactor]
            [othello.reactor-core :as reactor-core]
            [othello.view :as view]
            [clojure.string :as string]
            [reagent.core :refer [atom]]
            [reagent.dom]
            [cljs.test :refer-macros [is testing]]))

;; (enable-console-print!)

(defonce app-state
  (atom nil))

(defn new-game-click-callback [type size]
  (fn new-game-click [e]
    (reset! app-state (game/new-game type size))
    (reset! view/observations nil)))

(defn main-view []
  [:center
   [:h1 (string/capitalize
         (name
          (:type @app-state "Choose a game")))]
   [:h2
    (case (:status @app-state)
      :player-victory "You won! "
      :computer-victory "Computer wins. "
      :draw "Draw. "
      "")]
   [:div
    [:button
     {:on-click
      (new-game-click-callback :tictactoe 3)}
     "New Tic Tac Toe Game"]
    [:button
     {:on-click
      (new-game-click-callback :othello 8)}
     "New Othello Game"]
    [:button
     {:on-click
      (new-game-click-callback :reactor 4)}
     "New Reactor Game"]]

   (when @app-state
     [view/game-board! app-state])])

(defn ^:export main []
  (when-let [app (. js/document (getElementById "app"))]
    (reagent.dom/render [main-view] app)))

(main)

(defn on-js-reload []
  (main))
