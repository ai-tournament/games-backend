(ns games-api.daos.games-dao)

(defn games-list
  "Retrieves list of currently available games as a vector of maps [{:game-id :game-name}]"
  []
  [{:game-id "chess" :game-name "Chess"}])
