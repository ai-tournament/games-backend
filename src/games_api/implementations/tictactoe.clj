(ns games-api.implementations.tictactoe
  (:use games-api.protocols.game-protocol)
  (:use clojure.string))

(defrecord TicTacToe []
  Game
  (game-details [_] (str "{ name: \"Tic Tac Toe game!\""
                            "  desc: \"It' really cool :)\""
                            "}"))
  (initial-state [_] (str "["
                             (join ", " (vec (take 9 (repeat "\"E\""))))
                             "]"))
  (apply-move [_ game-state move] nil)
  (finished? [_ game-state] nil))