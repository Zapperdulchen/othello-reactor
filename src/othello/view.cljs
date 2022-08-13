(ns othello.view
  (:require [othello.tictactoe :as tictactoe]
            [othello.othello :as othello]
            [othello.game :as game]
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
      (game/player-move game i j))}])

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

(defn discs [i j]
  [:g {:fill "red"
       :transform
       (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
            "scale(0.3)")}
   [:circle
    {:r 0.45
     :cx 0
     :cy -0.5}]
   [:circle
    {:r 0.45
     :cx -0.7
     :cy 0.5}]
   [:circle
    {:fill "black"
     :r 0.45
     :cx 0.7
     :cy 0.5}]])

(defn squares [i j]
  [:g {:fill "red"
       :transform
       (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
            "scale(0.3)")}
   [:rect
    {:fill "green"
     :width 0.9
     :height 0.9
     :x -0.4
     :y -1.05
     :rx 0.08
     :ry 0.08}]
   [:rect
    {:width 0.9
     :height 0.9
     :x -1
     :y 0.1
     :rx 0.08
     :ry 0.08}]
   [:rect
    {:width 0.9
     :height 0.9
     :x 0.2
     :y 0.1
     :rx 0.08
     :ry 0.08}]])

(defn triangles [i j]
  [:g {:fill "red"
       :transform
       (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
            "scale(3)")}
   [:rect
    {:fill "green"
     :width 0.9
     :height 0.9
     :x -0.4
     :y -1.05
     :rx 0.08
     :ry 0.08}]
   [:rect
    {:width 0.9
     :height 0.9
     :x -1
     :y 0.1
     :rx 0.08
     :ry 0.08}]
   [:rect
    {:width 0.9
     :height 0.9
     :x 0.2
     :y 0.1
     :rx 0.08
     :ry 0.08}]])
(defn particles [i j p n]
  (let [p2c ["red" "blue" "green" "yellow"]]))

(defn piece [i j]
  [:g {:fill "red"
       :transform
       (str ;; "translate(" i "," j ") "
            "translate(" (+ 0.05 i) "," (+ 0.05 j) ") "
            "scale(0.004)")}
  ;; [:svg {:viewbox "0 0 500 500", :width "100", :height "100", :xmlns "http://www.w3.org/2000/svg"}
 [:g {:fill "red", :stroke "black", :stroke-width "3"}
  [:circle {:cx "50", :cy "50", :r "39", }];; :transform "translate(000,000)"}]
  ;; [:rect {:x "11", :y "11", :width "78", :height "78", :transform "translate(100,100)"}]
  ;; [:polygon {:points "11 89 50 11 89 89", :transform "translate(200,200)"}]
  ;; [:polygon {:points "50 0 83 39 50 78 17 39", :transform "translate(300,300)"}]
;;
  ]])

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
   [blank (atom {}) 0 1 "lightgrey"]
   [discs 0 1]
   [blank (atom {}) 1 1 "lightgrey"]
   [squares 1 1]
   [blank (atom {}) 2 1 "lightblue"]
   [piece 2 1]
  ;
   ])

(defn game-board [game]
  (let [{:keys [board-size background-color]} @game]
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
        (case x
          "B" [black-disc i j]
          "W" [discs i j]
          "C" [cross i j]
          "P" [circle i j]))))))

(defcard-rg othello-example
  [game-board (reagent/atom (othello/new-game 8))])

(defcard-rg tictactoe-example
  [game-board (reagent/atom (tictactoe/new-game 3))])

(defcard my-first-card
  (sab/html [:h1 "Devcards is freaking awesome!"]))

(println "inside view.cljs 12")
