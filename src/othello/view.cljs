(ns othello.view
  (:require [othello.game :as game :refer [meeple]]
            ;; [othello.othello :as othello]
            ;; [othello.tictactoe :as tictactoe]
            ;; [othello.reactor :as reactor]
            [reagent.core :as reagent]
            [sablono.core :as sab] ; del
            [devcards.core :refer-macros [defcard-rg defcard deftest]]
            [cljs.test :as test]))

(defn blank
  [game i j background-color]
  [:rect
   {:width 0.9
    :height 0.9
    :fill background-color
    :x (+ 0.05 i)
    :y (+ 0.05 j)
    :on-click
    (fn blank-click [e]
;;
      (game/player-move game i j))}])

;;
(defn circle [i j]
  [:circle
   {:r 0.35
    :stroke "green"
    :stroke-width 0.12
    :fill "none"
    :cx (+ 0.5 i)
    :cy (+ 0.5 j)}])

(defn cross [i j]
  [:g {:stroke "darkred"
       :stroke-width 0.4
       :stroke-linecap "round"
       :transform
       (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
            "scale(0.3)")}
   [:line {:x1 -1 :y1 -1 :x2 1 :y2 1}]
   [:line {:x1 1 :y1 -1 :x2 -1 :y2 1}]])

(defn black-disc [i j]
  [:circle
   {:r 0.3
    :fill "black"
    :cx (+ 0.5 i)
    :cy (+ 0.5 j)}])

(defn white-disc [i j]
  [:circle
   {:r 0.3
    :fill "white"
    :cx (+ 0.5 i)
    :cy (+ 0.5 j)}])

(defn piece [i j n color form]
  (let [form2comp {:circle [:circle {:r "0.4", :cx "0", :cy "-0.5"}]
                   :triangle [:polygon {:points "-0.4 -0.1 0 -0.9 0.4 -0.1"}]
                   :square [:polygon {:points "-0.4 -0.9 0.4 -0.9 0.4 -0.1 -0.4 -0.1"}]
                   :diamond [:polygon {:points "0 -0.9 -0.3 -0.5 0 -0.1 0.3 -0.5"}]}
        positions {0 nil 1 ["0,0.5"] 2 ["-0.5,0.1" "0.5,0.9"] 3 ["0,0" "-0.7,1" "0.7,1"]}
        comp (form2comp form)]
    (into [:g {:fill color
               :stroke color
               :stroke-linejoin "round"
               :stroke-width "0.08"
               :transform
               (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
                    "scale(0.3)")}]
          ;; debug
          (if (> n 3)
            [[:text {:x i :y j :transform "scale(0.1)"} (str n)]]
            (map #(vector (first comp) (assoc (second comp)
                                              :transform (str "translate(" % ")")))
                 (positions n))))))

(defn particles [i j n p]
  (let [player2color ["blue" "red" "green" "yellow"]
        player2form [:circle :triangle :square :diamond]]
    (piece i j n (player2color p) (player2form p))))

(defcard-rg board-example
  [:svg
   {:view-box "0 0 5 2"
    :width 500
    :height 100}
   [blank (atom {}) 0 0 "lightgrey"]
   [circle 1 0]
   [cross 2 0]
   [blank (atom {}) 3 0 "green"]
   [black-disc 3 0]
   [blank (atom {}) 4 0 "green"]
   [white-disc 4 0]
   [blank (atom {}) 0 1 "lightblue"]
   [piece 0 1 1 "red" :circle]
   [blank (atom {}) 1 1 "lightblue"]
   [piece 1 1 2 "blue" :square]
   [blank (atom {}) 2 1 "lightblue"]
   [piece 2 1 3 "green" :diamond]
   [blank (atom {}) 3 1 "lightblue"]
   [piece 3 1 3 "yellow" :triangle]
   [blank (atom {}) 4 1 "lightblue"]
   [particles 4 1 3 0]
  ;
   ])

(defmethod meeple :tictactoe [gamekey m i j]
  (case m
    "C" [cross i j]
    "P" [circle i j])
    )

(defmethod meeple :othello [gamekey m i j]
  (case m
    "B" [black-disc i j]
    "W" [white-disc i j]))

(defmethod meeple :reactor [gamekey m i j] 
  (if (= 0 (:count m))
    [:span] ;; empty
    (particles i j (:count m) (:color m))))

(defn game-board [game]
  (let [{:keys [type board-size background-color]} @game]
    (->
     [:svg
      {:view-box (str "0 0 " board-size " " board-size)
       :width 500
       :height 500}]
     (into
      (for [i (range board-size)
            j (range board-size)]
        [blank game i j background-color]))
     (into
      (for [i (range board-size)
            j (range board-size)
            :let [x (get-in (:board @game) [j i])]
            :when (not= " " x)]
        (meeple type x i j))))))

(defcard my-first-card
  (sab/html [:h1 "Devcards is freaking awesome!"]))

(defcard-rg othello-example
  [game-board (reagent/atom (game/new-game :othello 8))])

(defcard-rg tictactoe-example
  [game-board (reagent/atom (game/new-game :tictactoe 3))])

(defcard-rg reactor-example
  [game-board (reagent/atom (game/new-game :reactor 8))])

