(ns games-api.implementations.chess
  (:use games-api.protocols.game-protocol)
  (:use clojure.string))

(defrecord Chess []
  Game
  (game-details [_] (str "test chess"))
  (initial-state [_] (str "Move your pawn!"))
  (apply-move [this game-state move])
  (finished? [this game-state]))

