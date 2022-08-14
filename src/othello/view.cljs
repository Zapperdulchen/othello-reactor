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

(defn particles [i j p n]
  (let [p2c ["red" "blue" "green" "yellow"]]))

(defn form []
  {:circle [:circle {:r "0.4", :cx "0", :cy "-0.5"}]
   :triangle [:polygon {:points "-0.4 -0.1 0 -0.9 0.4 -0.1"}]
   :square [:polygon {:points "-0.4 -0.9 0.4 -0.9 0.4 -0.1 -0.4 -0.1"}]
   :diamond [:polygon {:points "0 -0.9 -0.3 -0.5 0 -0.1 0.3 -0.5"}]})

(def container [:g {:attr 1}])

;; ((fn mixit [n]
;;    (let [i [:piece {:attr 2}]
;;          l {0 nil 1 ["0,0"] 2 ["1,1" "1,2"] 3 ["2,1" "2,2" "2,3"]}]
;;      (into container
;;            (map #(vector (first i) (assoc (second i)
;;                                           :transform (str "translate=(" % ")"))) (\
;; get l n))))) 2)

(defn piece [i j n color form]
  (let [form2comp {:circle [:circle {:r "0.4", :cx "0", :cy "-0.5"}]
                   :triangle [:polygon {:points "-0.4 -0.1 0 -0.9 0.4 -0.1"}]
                   :square [:polygon {:points "-0.4 -0.9 0.4 -0.9 0.4 -0.1 -0.4 -0.1"}]
                   :diamond [:polygon {:points "0 -0.9 -0.3 -0.5 0 -0.1 0.3 -0.5"}]}
        positions {0 nil 1 ["0,0.5"] 2 ["-0.5,0.1" "0.5,0.9"] 3 ["0,0" "-0.7,1" "0.7,1"]}
        comp (form2comp form)]
    (+ 1 1)
    ;; (into [:g {:fill color
    ;;            :stroke color
    ;;            :stroke-linejoin "round"
    ;;            :stroke-width "0.08"
    ;;            :transform
    ;;            (str "translate(" (+ 0.5 i) "," (+ 0.5 j) ") "
    ;;                 "scale(0.3)")}]
          (map #(vector (first comp) (assoc (second comp)
                                            :transform (str "translate=(" % ")")))
               (positions n))
          ;; )
  ))

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
   [blank (atom {}) 1 1 "lightgrey"]
   [blank (atom {}) 2 1 "lightblue"]
   [piece 2 1 "black" :circle]
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
          "W" [white-disc i j]
          "C" [cross i j]
          "P" [circle i j]))))))

(defcard-rg othello-example
  [game-board (reagent/atom (othello/new-game 8))])

(defcard-rg tictactoe-example
  [game-board (reagent/atom (tictactoe/new-game 3))])

(defcard my-first-card
  (sab/html [:h1 "Devcards is freaking awesome!"]))

(println "inside view.cljs 12")
