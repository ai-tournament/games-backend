(ns games-api.implementations.tictactoe
  (:use games-api.protocols.game-protocol)
  (:use clojure.string))

(defrecord TicTacToe []
  Game
  (game-details [this] (str "{ name: \"Tic Tac Toe game!\""
                            "  desc: \"It' really cool :)\""
                            "}"))
  (initial-state [this] (str "["
                             (join ", " (vec (take 9 (repeat "\"E\""))))
                             "]"))
  (apply-move [this game-state move] nil)
  (finished? [this game-state] nil))