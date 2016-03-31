(ns games-api.implementations.chess
  (:import (games_api.protocols.game_protocol Game)))

(defrecord Chess []
  Game
  (game-details [this] (str "test chess"))
  (initial-state [this])
  (apply-move [this game-state move])
  (finished? [this game-state]))

