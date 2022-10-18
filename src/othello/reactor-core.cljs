(ns othello.reactor-core)

;; framework-independent core implementation

(def empty-field {:color nil, :count 0})

(defn make-board [width height]
  (vec (repeat height (vec (repeat width empty-field)))))

(defn get-particles [board x y]
  (get-in board [y x]))

(defn del-particles [board x y]
  (assoc-in board [y x] empty-field))

;; add-particle without validating first, necessary for fissions
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
  (println "fission-field" x y)
  (let [color (:color (get-particles board x y))]
    (reduce (fn [board [x y]] (force-particle board x y color))
            (del-particles board x y) ;; if there are more particles than adjacent fields
            ;; then particles will be wiped out, could be changed but I think
            ;; it's ok, it's a game not a physic simulation
            (adjacent-fields board x y))))

(defn fission-1-iteration [board]
  (let [fissions
        (for [y (range (count board))
              x (range (count (first board)))
              :when (fission? board x y)]
          [x y])]
    (if (empty? fissions)
      board (reduce (fn [board [x y]] (fission-field board x y)) board fissions))))

;; fissions can loop infinitely, so we need to detect loops
;; if a loop is detected we stop and return the board
;; assumption: if a loop occurs, only one player is left
(defn fission [board]
  (loop [board board
         cache #{board}
         n 1]
    (let [new-board (fission-1-iteration board)]
      (if (cache new-board) ;; true if loop detected or no fission happened(!)
        new-board
        (recur new-board (conj cache new-board) (inc n))))))

;; (defn particle-colors [board]
;;   (distinct (map :color (flatten board))))

(defn score [board]
  (dissoc
   (reduce (fn [score particle]
             (update score (:color particle) (fnil (partial + (:count particle)) 0)))
           {}
           (flatten board))
   nil))
