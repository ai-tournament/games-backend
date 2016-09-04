(ns games-api.repositories.game-repository
  (:require [monger.collection :as mc]
            [monger.core :as mg]))

(defn- with-db [op & args]
  (let [conn (mg/connect)
        db (mg/get-db conn "game-states")]
    (apply op db args)))

(defn update-game-state
  [game-id match-id new-game-state]
  (with-db mc/update game-id {:_id match-id} {:state new-game-state} {:upsert true}))

(defn retrieve-game-state
  [game-id match-id]
  (get (with-db mc/find-one-as-map game-id {:_id match-id}) :state))