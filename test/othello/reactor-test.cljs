(ns othello.reactor-test
  (:require
   [cljs.test :refer [is testing]]
   [devcards.core :refer-macros [deftest defcard-rg]]
   [othello.reactor-core :refer [add-particle]]
   [othello.reactor-core-test :refer [empty-board full-board specific-board]]
   [othello.game :as game :refer [win?]]
   [othello.view :as view]
   [reagent.core :as reagent]))

(defcard-rg reactor-example-turned-rogue
  (let [game (game/new-game :reactor 4)
        board (-> (:board game)
                  (othello.reactor-core/force-particle 1 1 0)
                  (othello.reactor-core/force-particle 1 1 0)
                  (othello.reactor-core/force-particle 1 1 0)
                  (othello.reactor-core/force-particle 1 1 0)
                  (othello.reactor-core/force-particle 1 1 0))
        rogue-game (assoc game :board board)]
    [view/game-board (reagent/atom rogue-game)]))

(deftest win?-test-1
  (let [game {:type :reactor
              :board empty-board}]
    (testing "win? - empty-board"
      (is (not (win? game :blue))))))

(deftest win?-test-2
  (let [game {:type :reactor
              :board full-board}]
    (testing "win? - full-board"
      (is (win? game :blue)))))

(deftest win?-test-3
  (let [game {:type :reactor
              :board (specific-board [[0 1 :blue 1]
                                                    [1 0 :blue 2]
                                                    [0 2 :blue 1]
                                                    [1 1 :blue 1]
                                                    [2 1 :blue 1]
                                                    [1 2 :blue 2]
                                                    [2 3 :red 1]
                                                    [2 2 :red 1]
                                                    [3 3 :red 1]
                                                    [3 1 :red 1]])}]
    (testing "win? - mid-play"
      (is (and (not (win? game :blue)) (not (win? game :red)))))))

(deftest win?-test-4
  (let [game {:type :reactor
              :board (specific-board
                                    (for [i (range (count empty-board))
                                          [j color] (map list
                                                         (range (count (empty-board 0)))
                                                         (cycle [:blue :red]))]
                                      [i j color 1]))}]
    (testing "win? - full-board doesn't mean end of game"
      (is (and (not (win? game :blue)) (not (win? game :red)))))))

(deftest win?-test-5
  (let [game {:type :reactor
              :board (add-particle
                      (specific-board
                       (for [i (range (count empty-board))
                             [j color] (map list
                                            (range (count (empty-board 0)))
                                            (cycle [:blue :red]))]
                         [i j color 1]))
                      0 0 :blue)}]
    (testing "win? - full-board doesn't mean end of game II"
      (is (and (not (win? game :blue)) (not (win? game :red)))))))
