(ns games-api.daos.games-dao)

(defn games-list
  "Retrieves list of currently available games as a vector of maps [{:game-id :game-name}]"
  []
  [{:game-id "tic-tac-toe" :game-name "Tic-Tac-Toe"}
   {:game-id "chess" :game-name "Chess"}])
