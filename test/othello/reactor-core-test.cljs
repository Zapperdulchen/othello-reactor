(ns othello.reactor-core-test
  (:require
   [cljs.test :refer [is testing]]
   [devcards.core :refer-macros [deftest]]
   [clojure.walk :refer [postwalk]]
   [othello.reactor-core :refer [make-board adjacent-fields add-particle get-particles fission? fission-field fission score]]))

(def empty-board (make-board 4 4))

;; the following function relies on the implementation details of the board
(defn set-particles [board x y color n]
  (assoc-in board [x y] {:color color :count n}))

(defn specific-board [fields]
  ;; fields = [[x y color n]*]
 (reduce #(apply set-particles %1 %2) empty-board fields)) 

(def full-board (let [fields (for [y (range (count empty-board))
                                   x (range (count (first empty-board)))]
                               [x y])]
                  (reduce
                   (fn [board [x y]]
                     (set-particles board x y :blue (count (adjacent-fields board x y))))
                   empty-board
                   fields)))

(deftest full-board-1
  (testing "full-board (test variable)"
    (is (= [[2 3 3 2]
            [3 4 4 3]
            [3 4 4 3]
            [2 3 3 2]]
          (postwalk #(if (and (coll? %) (= :blue (:color %))) (:count %) %) full-board)))))

(deftest make-board-1
  (testing "make-board"
    (is (= [[{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]] 
           empty-board))))

(deftest make-board-2
  (testing "make-board returns vector of vectors"
    (is (and (= (type []) (type empty-board))
             (= (type []) (type (first empty-board)))))))

(deftest add-particle-1
  (testing "add-particle allows adding particle to an empty field"
    (is (= [[{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color :blue, :count 1}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]]
           (add-particle empty-board 1 2 :blue)))))

(deftest add-particle-2
  (testing "add-particle allows adding particle to another particle of the same color"
    (is (= [[{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color :blue, :count 2}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]]
           (add-particle (add-particle empty-board 1 2 :blue) 1 2 :blue)))))

(deftest add-particle-3
  (testing "add-particle doesn't allow adding particle with wrong color"
    (is (= [[{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color :blue, :count 1}
             {:color nil, :count 0}
             {:color nil, :count 0}]
            [{:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}
             {:color nil, :count 0}]]
           (add-particle (add-particle empty-board 1 2 :blue) 1 2 :green)))))

(deftest get-particles-1
  (testing "get-particles"
    (is (= {:color nil :count 0} (get-particles empty-board 3 3)))))

(deftest get-particles-3
  (testing "get-particles"
    (is (= {:color :blue :count 1} (get-particles (add-particle empty-board 1 2 :blue) 1 2)))))

(deftest get-particles-2
  (testing "get-particles"
    (is (= {:color :blue :count 2} (get-particles (add-particle (add-particle empty-board 1 2 :blue) 1 2 :blue) 1 2)))))

(deftest get-particles-4
  (testing "get-particles returns nil if coordinates are out of bound"
    (is (nil? (get-particles empty-board 4 4)))))

(deftest adjacent-fields-1
  (testing "adjacent-fields"
    (is (= #{[0 1] [2 1] [1 0] [1 2]} (set (adjacent-fields empty-board 1 1))))))

(deftest adjacent-fields-2
  (testing "adjacent-fields"
    (is (= #{[0 3] [2 3] [1 2]} (set (adjacent-fields empty-board 1 3))))))

(deftest adjacent-fields-3
  (testing "adjacent-fields"
    (is (= #{[2 3] [3 2]} (set (adjacent-fields empty-board 3 3))))))

(deftest fission?-1
  (testing "fission? - no fission on empty board"
    (is (empty?
         (for [y (range (count empty-board))
               x (range (count (first empty-board)))
               :when (fission? empty-board x y)]
           [x y])))))

(deftest fission?-2
  (testing "fission? - everywhere fission on full board"
    (let [fields (for [y (range (count empty-board))
                       x (range (count (first empty-board)))]
                   [x y])]
      (is (every? (partial apply fission? full-board) fields)))))

(deftest fission-field-test-1
  (testing "fission-field-test"
    (let [fields [[1 1 :blue 4]
                  [1 2 :green 1]
                  [3 3 :red 2]]
          board (specific-board fields)]
      (is (= (specific-board [[0 1 :blue 1]
                              [2 1 :blue 1]
                              [1 0 :blue 1]
                              [1 2 :blue 2]
                              [3 3 :red 2]])
             (fission-field board 1 1))))))

(deftest fission-field-test-2
  (testing "fission-field-test"
    (let [fields [[1 1 :blue 4]
                  [1 2 :green 1]
                  [3 3 :red 2]]
          board (specific-board fields)]
      (is (= (specific-board [[1 1 :blue 4]
                              [1 2 :green 1]
                              [2 3 :red 1]
                              [3 2 :red 1]])
             (fission-field board 3 3))))))

(deftest fission-test-1
  (testing "fission-test without chain-reaction"
    (let [fields [[1 1 :blue 4]
                  [1 2 :green 1]
                  [3 3 :red 2]]
          board (specific-board fields)]
      (is (= (specific-board [[0 1 :blue 1]
                              [2 1 :blue 1]
                              [1 0 :blue 1]
                              [1 2 :blue 2]
                              [2 3 :red 1]
                              [3 2 :red 1]])
             (fission board))))))

(deftest fission-test-2
  (testing "fission-test with chain-reaction"
    (let [fields [[1 1 :blue 4]
                  [1 2 :green 1]
                  [0 1 :white 2]
                  [0 0 :red 1]
                  [3 3 :red 2]
                  [3 2 :blue 2]]
          board (specific-board fields)]
      (is (= (specific-board [[0 1 :blue 1]
                              [1 0 :blue 2]
                              [0 2 :blue 1]
                              [1 1 :blue 1]
                              [2 1 :blue 1]
                              [1 2 :blue 2]
                              [2 3 :red 1]
                              [2 2 :red 1]
                              [3 3 :red 1]
                              [3 1 :red 1]])
             (fission board))))))

(deftest score-test-1
  (testing "score - empty-board"
    (is (= {} (score empty-board)))))

(deftest score-test-2
  (testing "score - full-board"
    (is (= {:blue 48} (score full-board)))))

(deftest score-test-3
  (testing "score - specific-board"
    (is (= {:blue 8 :red 4} (score (specific-board [[0 1 :blue 1]
                                              [1 0 :blue 2]
                                              [0 2 :blue 1]
                                              [1 1 :blue 1]
                                              [2 1 :blue 1]
                                              [1 2 :blue 2]
                                              [2 3 :red 1]
                                              [2 2 :red 1]
                                              [3 3 :red 1]
                                              [3 1 :red 1]]))))))

