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
  {:tictactoe (TicTacToe.)
   :chess     (Chess.)})

(defn get-game-instance
  [game-id]
  (if-let [game-instance ((keyword game-id) games-instances)]
    game-instance))

(defroutes app-routes
           (context "/games" []
             (defroutes games-routes
                        (GET "/" []
                          (json/write-str (games-list)))
                        (context "/:game-id" [game-id]
                          (let [game-instance (get-game-instance game-id)]
                            (GET "/" []
                              (json/write-str (game-details game-instance)))
                            (context "/:match-id" [match-id]
                              (let [game-state (get-game-state game-id match-id)
                                    current-state (or game-state (initial-state game-instance))]
                                (GET "/" []
                                  (json/write-str current-state))
                                (POST "/apply-move" request
                                  (let [move (:body request)
                                        move-result (apply-move game-instance current-state move)]
                                    (when (:is-valid move-result)
                                      (update-game-state game-id match-id (:new-state move-result)))
                                    (json/write-str move-result)))))))
                        (route/not-found "Not Found"))))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
