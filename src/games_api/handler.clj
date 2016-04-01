(ns games-api.handler
  (:require [games-api.implementations.tictactoe])
  (:import games_api.implementations.tictactoe.TicTacToe)
  (:require [games-api.implementations.chess])
  (:import games_api.implementations.chess.Chess)
  (:use games-api.daos.games-dao)
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:use games-api.protocols.game-protocol)
  (:require [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [compojure.route :as route]
            [clojure.data.json :as json]))

  (def chess-creation "(Chess.)")
  (def games-instances
    {
     "tictactoe" (TicTacToe.)
     "chess"     (eval (read-string chess-creation))
     })

  (defroutes app-routes
             (context "/games" []
               (defroutes games-routes
                          (GET "/" []
                            (json/write-str (games-list )))
                          (GET "/:game-id" [game-id]
                            (game-details (get games-instances game-id)))
                          (GET "/:game-id/initial-state" [game-id]
                            (initial-state (get games-instances game-id)))
                          (route/not-found "Not Found"))))

  (def app
    (-> (handler/api app-routes)
        (middleware/wrap-json-body)
        middleware/wrap-json-response))
