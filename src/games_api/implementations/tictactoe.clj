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

  (finished? [_ game-state] nil))