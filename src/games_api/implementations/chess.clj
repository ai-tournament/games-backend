(ns games-api.implementations.chess
  (:use games-api.protocols.game-protocol))


(defrecord Chess []
  Game
  (game-details [_] "Awesome chess game!")
  (initial-state [_] "Move your pawn!")
  (is-move-valid? [this game-state move])
  (apply-move [this game-state move])
  (is-finished? [this game-state])
  (get-winner [_ game-state]))

