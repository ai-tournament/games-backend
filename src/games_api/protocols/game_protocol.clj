(ns games-api.protocols.game-protocol)

(defprotocol Game
  (game-details [this])
  (initial-state [this])
  (is-move-valid? [this game-state move])
  (apply-move [this game-state move])
  (is-finished? [this game-state])
  (get-winner [_ game-state]))