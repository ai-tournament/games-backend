(ns games-api.daos.games-dao
  (:require [monger.collection :as mc]
            [monger.core :as mg]))

(defn with-db [op & args]
  (let [conn    (mg/connect)
        db      (mg/get-db conn "game-states")]
    (apply op db args)))

(defn games-list
  "Retrieves list of currently available games as a vector of maps [{:game-id :game-name}]"
  []
  [{:game-id "tictactoe" :game-name "Tic-Tac-Toe"}
   {:game-id "chess" :game-name "Chess"}])

(defn update-game-state
  [game-id match-id new-game-state]
  (with-db mc/update game-id {:_id match-id} {:state new-game-state} {:upsert true}))

(defn get-game-state
  [game-id match-id]
  (get (with-db mc/find-one-as-map game-id {:_id match-id}) :state))