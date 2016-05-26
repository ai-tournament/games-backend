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

  (def games-instances
    {
     "tic-tac-toe" (TicTacToe.)
     "chess"     (Chess.)
     })

  (def games-states (atom
    {
     "tictactoe" []
     }))

  (defn update-game-state
    [game-id new-game-state]
    (swap! games-states #(assoc % game-id new-game-state)))

  (defroutes app-routes
             (context "/games" []
               (defroutes games-routes
                          (GET "/" []
                            (json/write-str (games-list)))
                          (context "/:game-id" [game-id]
                            (GET "/" []
                              (json/write-str (game-details (get games-instances game-id))))
                            (GET "/initial-state" []
                              (update-game-state game-id (initial-state (get games-instances game-id)))
                              (json/write-str (get (deref games-states) game-id)))
                            (POST "/apply-move" request
                              (let [current-state (get (deref games-states) game-id)
                                    move (:body request)
                                    apply-move-response (apply-move (get games-instances game-id) current-state move)]
                                (when (= (get apply-move-response "status") "ok")
                                  (update-game-state game-id (get apply-move-response "new-state")))
                                (json/write-str apply-move-response))))
                          (route/not-found "Not Found"))))

  (def app
    (-> (handler/api app-routes)
        (middleware/wrap-json-body)
        middleware/wrap-json-response))
