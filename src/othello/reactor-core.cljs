(ns othello.reactor-core)

;; framework-independent core implementation

(def empty-field {:color nil, :count 0})

(defn make-board [width height]
  (vec (repeat height (vec (repeat width empty-field)))))

(defn get-particles [board x y]
  (get-in board [y x]))

(defn del-particles [board x y]
  (assoc-in board [y x] empty-field))

(defn force-particle [board x y color]
  (assoc-in board [y x]
            (assoc 
             (update (get-particles board x y) :count inc)
             :color color)))

(defn valid-move? [particles color]
  (or (#{color} (:color particles))
      (zero? (:count particles))))

(defn add-particle [board x y color]
  (let [particles (get-particles board x y)]
    (if (valid-move? particles color)
      (force-particle board x y color)
      board)))

(def all-adjacent-fields
  (memoize
   (fn [width height]
     (let [empty-board (make-board width height)]
       (print "inside all-adjacent-fields - this should only happen once")
       (->> (for [y0 (range height)
                  x0 (range width)]
              (for [[dx dy] [[1 0] [-1 0] [0 1] [0 -1]]
                    :let [x1 (+ x0 dx) y1 (+ y0 dy)]
                    :when (get-particles empty-board x1 y1)]
                [x1 y1]))
            (partition width) ; make grid
            (map vec) ; make vector of vectors
            vec)))))

(defn adjacent-fields [board x y]
  (let [height (count board)
        width (count (first board))]
    (get-in (all-adjacent-fields width height) [y x])))

(defn fission? [board x y]
  (>= (:count (get-particles board x y))
     (count (adjacent-fields board x y))))

(defn fission-field [board x y]
  (let [color (:color (get-particles board x y))]
    (reduce (fn [board [x y]] (force-particle board x y color))
            (del-particles board x y)
            (adjacent-fields board x y))))

(defn fission [board]
  (loop [board board
         n 1]
    (println board)
    (println n)
    (let [fissions
          (for [y (range (count board))
                x (range (count (first board)))
                :when (fission? board x y)]
            [x y])]
      (if (empty? fissions)
        board
        (recur (reduce (fn [board [x y]] (fission-field board x y)) board fissions) (inc n))))))


;; (defn particle-colors [board]
;;   (distinct (map :color (flatten board))))

(defn score [board]
  (dissoc
   (reduce (fn [score particle]
             (update score (:color particle) (fnil (partial + (:count particle)) 0)))
           {}
           (flatten board))
   nil))

(comment
  (= (type []) (type [1]))
  (add-particle (make-board 4 4) 1 2 :blue)

  (assoc-in (make-board 4 4) [2 1] [:blue 1])

  (pp/pprint (make-board 4 4))

  (map (fn [x y] (+ x y)) (range 4) (range 4))

  ;;
  )



(println "inside othello.reactor")
