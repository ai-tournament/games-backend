(ns games-api.protocols.game-protocol)

(defprotocol Game
  (game-details [this])
  (initial-state [this])
  (apply-move [this game-state move])
  (finished? [this game-state]))