(ns games-api.implementations.tictactoe
  (:use games-api.protocols.game-protocol))

(defn get-position-value [game-state pos]
  (let [[x y] pos]
    ((game-state y) x)
    )
  )

(defn set-position-value [game-state pos marker]
  (let [[x y] pos]
    (assoc game-state y (assoc (game-state y) x marker))
    )
  )

(defn- empty-position? [game-state pos]
  (= (get-position-value game-state pos) "E")
  )

(defn get-markers []
  ["X" "O"]
  )

(defn all-equals? [row marker]
  (let [[a b c] row]
    (= marker a b c))
  )

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
    (for [x (range 0 3) y (range 0 3) :when (filter x y)] (get-position-value game-state [x y])))
  (let [first-diag (get-diag #(= %1 %2))
        second-diag (get-diag #(= (+ %1 %2) 2))]
    (or (all-equals? first-diag marker) (all-equals? second-diag marker))))

(defn all-equals-board? [game-state marker]
  (or (all-equals-diag? game-state marker)
      (all-equals-columns? game-state marker)
      (all-equals-rows? game-state marker))
  )

(defn index-of [elem coll]
  (first (keep-indexed #(if (= elem %2) %1) coll))
  )

(defn- winner [marker game-state]
  (all-equals-board? game-state marker)
  )

(defrecord TicTacToe []
  Game
  (game-details [_] { "name" "Tic Tac Toe game!"
                      "desc" "It's really cool :)"
                      "moves" {

                               }
                    })
  (initial-state [_] (vec (take 3 (repeat (vec (take 3 (repeat "E")))))))

  (apply-move [_ game-state move]
    (let [pos (get move "marker-position")
          player-id (dec (get move "player-id"))]
      (if (or (not (empty-position? game-state pos))
              (get (finished _ game-state) "finished"))
        {"status" "error"
         "new-state" game-state}
        { "status"  "ok"
          "new-state" (set-position-value game-state pos ((get-markers) player-id))
        })))

  (finished [_ game-state]
    (let [winner-id (cond
                      (winner "X" game-state) 1
                      (winner "O" game-state) 2
                      :else nil)]
      {"finished"  (not (= winner-id nil))
       "winner-id" winner-id
       }
      )))
