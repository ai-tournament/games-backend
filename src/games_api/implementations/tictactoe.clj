(ns games-api.implementations.tictactoe
  (:use games-api.protocols.game-protocol))

(defrecord TicTacToe []
  Game
  (game-details [_] { "name" "Tic Tac Toe game!"
                      "desc" "It's really cool :)"
                      "moves" {

                               }
                    })
  (initial-state [_] (vec (take 9 (repeat "E"))))

  (apply-move [_ game-state move]
    (def markers ["X" "O"])
    (let [pos (- (get move "marker-position") 1)
          player-id (- (get move "player-id") 1)]
      (if (not (= (get game-state pos) "E"))
        {"status" "error"}
        { "status"  "ok"
          "new-state" (assoc game-state pos (get markers player-id))
        })))

  (finished? [_ game-state]
    (defn empty-marker? [x] (= "E" x))
    (def select-values (partial (comp vals select-keys) game-state))
    (or (not-any? empty-marker? game-state)
        (let [strikes [[0 1 2] [3 4 5] [ 6 7 8] [0 3 6] [1 4 7] [2 5 8] [0 4 8] [2 4 6]]]
          (defn all-X-marker [subcol] (every? (partial = "X") subcol))
          (defn all-O-marker [subcol] (every? (partial = "O") subcol))
          (defn all-same-marker [indices] (or
                                                (all-X-marker (select-values indices))
                                                (all-O-marker (select-values indices))))
          (some (partial = true) (map all-same-marker strikes))))))