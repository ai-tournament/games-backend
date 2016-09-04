(ns games-api.engines.game-engine
  (:require [games-api.implementations.tictactoe])
  (:import games_api.implementations.tictactoe.TicTacToe)
  (:require [games-api.implementations.chess])
  (:import games_api.implementations.chess.Chess)
  (:require [games-api.repositories.game-repository :as repo])
  (:use games-api.protocols.game-protocol))

(def games-instances
  {:tictactoe (TicTacToe.)
   :chess     (Chess.)})

(defn get-game-instance
  [game-id]
  (if-let [game-instance ((keyword game-id) games-instances)]
    game-instance))

(defn get-games-list
  "Retrieves list of currently available games as a vector of maps [{:game-id :game-name}]"
  []
  [{:game-id "tictactoe" :game-name "Tic-Tac-Toe"}
   {:game-id "chess" :game-name "Chess"}])

(defn get-game-details
  [game-id]
  (game-details (get-game-instance game-id)))

(defn get-game-state
  [game-id match-id]
  (let [game-instance (get-game-instance game-id)
        saved-state (repo/retrieve-game-state game-id match-id)
        current-state (or saved-state (initial-state game-instance))]
    current-state))

(defn apply-move-to
  [game-id match-id move]
  (let [game-instance (get-game-instance game-id)
        saved-state (get-game-state game-id match-id)
        current-state (or saved-state (initial-state game-instance))
        move-result (apply-move game-instance current-state move)]
    (when (:is-valid move-result)
      (repo/update-game-state game-id match-id (:new-state move-result)))
    move-result))