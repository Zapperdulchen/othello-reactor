(ns othello.view
  (:require-macros
   [othello.slurp :refer [slurp-svg]])
  (:require [othello.game :as game :refer [meeple can-move?]]
            ;; [othello.othello :as othello]
            ;; [othello.tictactoe :as tictactoe]
            ;; [othello.reactor :as reactor]
            [othello.reactor-core :as reactor-core]
            [reagent.core :as reagent]
            [devcards.core :refer-macros [defcard-rg defcard deftest]]
            [othello.scratchpad]
            [cljs.test :as test]))

(def board-size-in-px 500)

(defonce observations
  (atom nil))

(defn observe-fission! [i j s]
  ;; register when fission in [i j] has started (s = true) or stopped (s = nil)
  (if s
    (swap! observations update :fission assoc [i j] s)
    (swap! observations update :fission dissoc [i j])))

(defn blank [game i j background-color]
  [:rect
   {:width 0.9
    :height 0.9
    :fill background-color
    :x (+ 0.05 i)
    :y (+ 0.05 j)
    :on-click
    (fn blank-click [e]
      (let [fission? reactor-core/fission?
            fission-field reactor-core/fission-field]
        (with-redefs
         [reactor-core/fission?
          (fn [& args]
            (let [fission-test (apply fission? args)]
              (when fission-test
                (observe-fission! i j true))
              fission-test))
          reactor-core/fission-field
          (fn [& args]
            (observe-fission! i j nil)
            (apply fission-field args))]
          (game/player-move game i j))))}])

;; (defn fission [i j]
;;   [:rect
;;    {:width 0.9
;;     :height 0.9
;;     :fill "red"
;;     :x (+ 0.05 i)
;;     :y (+ 0.05 j)}])

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

(def player2color ["blue" "red" "green" "yellow"])

(defn particles [i j n p]
  (let [player2form [:circle :triangle :square :diamond]]
    (piece i j n (player2color p) (player2form p))))

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

;; (defn fission-signal [game]
;;   (let [{:keys [fission? :background-color]} @game
;;         color (if fission? (player2color fission?) background-color)]
;;     [:div {:style {:position "relative" :width 100 :float "left"
;;                    :padding-top "20px" :padding-left "10px"}}
;;      [:svg {:view-box "0 0 117.829 103.684", :y "0", :x "0"
;;             :style {:position "absolute"}}
;;       [:g {:transform "translate(-419.804 -221.2405)"}
;;        [:path {:fill color, :d "m477.85 221.24c-1.881 0.291-3.534 1.402-4.479 3.047l-52.635 91.188c-1.101 1.916-1.118 4.303 0 6.231 1.118 1.929 3.198 3.142 5.448 3.142h105.27c2.25 0 4.332-1.211 5.449-3.142 1.117-1.93 1.102-4.315 0-6.231l-52.635-91.188c-1.3-2.25-3.87-3.45-6.43-3.05z"}]]]
;;      [:img {:src "img/explosion_wo_background.svg"
;;             :style {:position "relative" :y "0" :x "0"
;;                     "z-index" "1"}}]]))

;; (defn fission-sign [i j x board-size-in-pieces]
;;   (let [color (player2color (:color x))
;;         width (/ board-size-in-px board-size-in-pieces)]
;;     (println "fission " color)
;;     [:div {:class "fission-sign"}
;;      [:div {:style {:position "absolute" :width (- width 14) :left (+ 20 (* j width)) :top (+ 15 (* i width))}}
;;       [:svg {:view-box "0 0 117.829 103.684", :y "0", :x "0"
;;              :style {:position "absolute"}}
;;        [:g {:transform "translate(-419.804 -221.2405)"}
;;         [:path {:fill color, :d "m477.85 221.24c-1.881 0.291-3.534 1.402-4.479 3.047l-52.635 91.188c-1.101 1.916-1.118 4.303 0 6.231 1.118 1.929 3.198 3.142 5.448 3.142h105.27c2.25 0 4.332-1.211 5.449-3.142 1.117-1.93 1.102-4.315 0-6.231l-52.635-91.188c-1.3-2.25-3.87-3.45-6.43-3.05z"}]]]
;;       [:img {:src "img/explosion_wo_background.svg"
;;              :style {:position "relative" :y "0" :x "0"
;;                      "z-index" "1"}}]]]))

(defn fission-sign [i j x] 
  (let [color (player2color (:color x))]
    (println "fission " color)
    [:svg {:view-box "0 0 117.829 103.684"
           :width 0.9
           :height 0.9
           :x (+ 0.05 i)
           :y (+ 0.05 j)}
     [:g {:transform "translate(-419.804 -221.2405)"}
      [:path {:fill color, :d "m477.85 221.24c-1.881 0.291-3.534 1.402-4.479 3.047l-52.635 91.188c-1.101 1.916-1.118 4.303 0 6.231 1.118 1.929 3.198 3.142 5.448 3.142h105.27c2.25 0 4.332-1.211 5.449-3.142 1.117-1.93 1.102-4.315 0-6.231l-52.635-91.188c-1.3-2.25-3.87-3.45-6.43-3.05z"}]]
     [:image {:href "img/explosion_wo_background.svg"
              :width "100%" :height "100%" ; for unknown reasons this is not the default
              }]]))

(defn game-board [game obs]
  (let [{:keys [type board board-size background-color]} @game
        board-wo-extras
        (->
         [:svg
          {:view-box (str "0 0 " board-size " " board-size)
           :width board-size-in-px
           :height board-size-in-px}]
         (into
          (for [i (range board-size)
                j (range board-size)]
            [blank game i j background-color]))
         (into
          (for [i (range board-size)
                j (range board-size)
                :let [x (get-in (:board @game) [j i])]
                :when (not= " " x)]
            [meeple type x i j])))]
    (into board-wo-extras
          (when (= type :reactor)
            (let [{:keys [fission]} @obs]
              (for [i (range board-size)
                    j (range board-size)
                    :let [x (get-in board [j i])
                          f (get-in fission [[i j]])]
                    :when (some? f)]
                [fission-sign i j x]))))))

(defn game-board! [game]
  (game-board game observations))


;; Tests

(def temp-atom (reagent/atom 5))

(defn sleep [f ms]
  (js/setTimeout f ms))

(defcard-rg page
  [:div
   [:p @temp-atom]
   [:button
    {:on-click
     ;; (fn [] (swap! temp-atom inc) (println "+++"))
     ;; (fn [] (js/setTimeout #(swap! temp-atom inc) 10000) (println "+++"))
     (fn [] (js/setTimeout #(println "+++") 10000))}
    "Click me!"]])


(defcard-rg fission-example
  [game-board
   (reagent/atom
    (-> (game/new-game :reactor 4)
        (can-move?  1 1 0) ; game i j player
        (can-move?  3 3 0)))
   (reagent/atom {:fission {[0 1] true [1 0] true
                            [3 3] true}})])

;; (defcard-rg board-example
;;   [:svg
;;    {:view-box "0 0 5 2"
;;     :width 500
;;     :height 100}
;;    [blank (atom {}) 0 0 "lightgrey"]
;;    [circle 1 0]
;;    [cross 2 0]
;;    [blank (atom {}) 3 0 "green"]
;;    [black-disc 3 0]
;;    [blank (atom {}) 4 0 "green"]
;;    [white-disc 4 0]
;;    [blank (atom {}) 0 1 "lightblue"]
;;    [piece 0 1 1 "red" :circle]
;;    [blank (atom {}) 1 1 "lightblue"]
;;    [piece 1 1 2 "blue" :square]
;;    [blank (atom {}) 2 1 "lightblue"]
;;    [piece 2 1 3 "green" :diamond]
;;    [blank (atom {}) 3 1 "lightblue"]
;;    [piece 3 1 3 "yellow" :triangle]
;;    [blank (atom {}) 4 1 "lightblue"]
;;    [particles 4 1 3 0]
;;   ;
;;    ])

;; (defcard-rg svg-example
;;   [:div
;;    [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore"]
;;    [:div {:style {:position "relative" :width 100}}
;;     [:svg {:view-box "0 0 117.829 103.684", :y "0", :x "0"
;;            :style {:position "absolute"}}
;;      [:g {:transform "translate(-419.804 -221.2405)"}
;;       [:path {:fill "#AAAAAA", :d "m477.85 221.24c-1.881 0.291-3.534 1.402-4.479 3.047l-52.635 91.188c-1.101 1.916-1.118 4.303 0 6.231 1.118 1.929 3.198 3.142 5.448 3.142h105.27c2.25 0 4.332-1.211 5.449-3.142 1.117-1.93 1.102-4.315 0-6.231l-52.635-91.188c-1.3-2.25-3.87-3.45-6.43-3.05z"}]]]
;;     [:img {:src "img/explosion_wo_background.svg"
;;            :style {:position "relative" :y "0" :x "0"
;;                    "z-index" "1"}}]]
;;    [:p "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore"]])

;; (defcard-rg othello-example
;;   [game-board (reagent/atom (game/new-game :othello 8)) (reagent/atom nil)])

;; (defcard-rg tictactoe-example
;;   [game-board (reagent/atom (game/new-game :tictactoe 3)) (reagent/atom nil)])

;; (defcard-rg reactor-example
;;   [game-board (reagent/atom (game/new-game :reactor 8)) (reagent/atom nil)])

;; ;; (defcard-rg svg-example
;; ;;   (slurp-svg "resources/public/img/explosion_background.svg"))

;; ;; (defcard-rg text-example
;; ;;   [:p (print-str (slurp-svg "resources/public/img/explosion_background.svg"))]
;; ;;   ;; (slurp-svg "resources/public/img/explosion_background.svg")
;; ;;   )


