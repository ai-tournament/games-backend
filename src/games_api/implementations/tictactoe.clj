(ns games-api.implementations.tictactoe
  (:use games-api.protocols.game-protocol))

(defn get-position-value [game-state pos]
  (let [{:keys [x y]} pos]
    ((game-state y) x)))

(defn set-position-value [game-state pos marker]
  (let [{:keys [x y]} pos]
    (assoc game-state y (assoc (game-state y) x marker))))

(defn- empty-position? [game-state pos]
  (= (get-position-value game-state pos) "E"))

(defn get-markers []
  ["X" "O"])

(defn all-equals? [row marker]
  (let [[a b c] row]
    (= marker a b c)))

(defn all-equals-columns? [game-state marker]
  (loop [board game-state]
    (if (= board '(() () ()))
      false
      (if (all-equals? (map first board) marker)
        true
        (recur (map rest board))))))

(defn all-equals-rows? [game-state marker]
  (loop [board game-state]
    (if (= board '())
      false
      (if (all-equals? (first board) marker)
        true
        (recur (rest board))))))

(defn all-equals-diag? [game-state marker]
  (defn get-diag [filter]
    (for [x (range 0 3) y (range 0 3) :when (filter x y)] (get-position-value game-state {:x x :y y})))
  (let [first-diag (get-diag #(= %1 %2))
        second-diag (get-diag #(= (+ %1 %2) 2))]
    (or (all-equals? first-diag marker) (all-equals? second-diag marker))))

(defn all-equals-board? [game-state marker]
  (or (all-equals-diag? game-state marker)
      (all-equals-columns? game-state marker)
      (all-equals-rows? game-state marker)))

(defn index-of [elem coll]
  (first (keep-indexed #(if (= elem %2) %1) coll)))

(defn- is-winner? [marker game-state]
  (all-equals-board? game-state marker))

(defn- is-board-full?
  [game-state]
  (empty? (filter (fn[row](some #(= % "E") row)) game-state)))

(defrecord TicTacToe []
  Game
  (game-details [_] {:name  "Tic Tac Toe game!"
                     :desc  "It's really cool :)"
                     :moves {}})

  (initial-state [_] (vec (take 3 (repeat (vec (take 3 (repeat "E")))))))

  (is-move-valid?
    [_ game-state move]
    (let [pos (:marker-position move)]
      (or (not (empty-position? game-state pos))
          (is-finished? _ game-state))))

  (apply-move [_ game-state move]
    (let [player-id (:player-id move)
          pos (:marker-position move)]
      (if (is-move-valid? _ game-state move)
        {:is-valid  false
         :new-state game-state}
        {:is-valid  true
         :new-state (set-position-value game-state pos ((get-markers) player-id))})))

  (get-winner [_ game-state]
    (let [winner-id (cond
                      (is-winner? "X" game-state) 0
                      (is-winner? "O" game-state) 1
                      :else nil)]
      winner-id))

  (is-finished?
    [_ game-state]
    (or (get-winner _ game-state) (is-board-full? game-state))))
