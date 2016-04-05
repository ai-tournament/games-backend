(ns games-api.implementations.chess
  (:use games-api.protocols.game-protocol))


(defrecord Chess []
  Game
  (game-details [_] "test chess")
  (initial-state [_] "Move your pawn!")
  (apply-move [this game-state move])
  (finished? [this game-state]))

