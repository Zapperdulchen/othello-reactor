(ns othello.reactor
  (:require
   [othello.game :as game :refer [draw? can-move? available? win? new-game]]
   [othello.reactor-core :refer [make-board add-particle score]]))

(defmethod new-game :reactor [game-key n]
  {:type game-key
   :status :in-progress
   :background-color "grey"
   :player 0
   :computer 1
   :board-size n
   :board
   (-> (make-board n n)
       (add-particle (quot n 2) (quot n 2) 0)
       (add-particle (dec (quot n 2)) (quot n 2) 1)
       (add-particle (quot n 2) (dec (quot n 2)) 1)
       (add-particle (dec (quot n 2)) (dec (quot n 2)) 0))})

(defmethod can-move? :reactor
  [{:keys [board] :as game} x y player]
  ;; though named like a boolean function this returns the board after the move
  ;; was done or nil if the move is not valid
  (let [new-board (add-particle board x y player)]
    (when (not= board new-board)
      (assoc game :board new-board))))

(defmethod draw? :reactor
  [{:keys [computer player] :as game}]
  (and (empty? (game/available-moves game computer))
       (empty? (game/available-moves game player))))

(defmethod win? :reactor
  [{:keys [board] :as game} player]
  (let [s (score board)]
    (and (= 1 (count s))
         (s player))))

