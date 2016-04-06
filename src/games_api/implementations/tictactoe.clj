(ns games-api.implementations.tictactoe
  (:use games-api.protocols.game-protocol))

(defn- winner [game-state]
  (def select-values (partial (comp vals select-keys) game-state))
  (let [runs [[0 1 2] [3 4 5] [ 6 7 8] [0 3 6] [1 4 7] [2 5 8] [0 4 8] [2 4 6]]]
    (defn all-same-marker [indices] (and
                                      (apply = (select-values indices))
                                      (not (every? #(= "E" %) (select-values indices)))))
    (let [matching-run (first (filter #(all-same-marker %) runs))]
      (get game-state (first matching-run)))))

(defn- empty-position? [game-state pos]
  (= (get game-state pos) "E"))

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
      (if (or (not (empty-position? game-state pos))
              (get (finished _ game-state) "finished"))
        {"status" "error"
         "new-state" game-state}
        { "status"  "ok"
          "new-state" (assoc game-state pos (get markers player-id))
        })))

  (finished [_ game-state]
    (let [winner-result (winner game-state)]
      {"finished"  (not (= winner-result nil))
       "winner-id" (if winner-result
                     (if (= winner-result "X") 1 2)
                     nil)
       })))