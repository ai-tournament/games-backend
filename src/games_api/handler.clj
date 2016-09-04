(ns games-api.handler
  (:require [games-api.implementations.tictactoe])
  (:require [games-api.implementations.chess])
  (:require [games-api.engines.game-engine :as engine])
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:require [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [compojure.route :as route]
            [clojure.data.json :as json]))

(defroutes app-routes
           (context "/games" []
             (defroutes games-routes
                        (GET "/" []
                          (json/write-str (engine/get-games-list)))
                        (context "/:game-id" [game-id]
                          (GET "/" []
                            (json/write-str (engine/get-game-details game-id)))
                          (context "/:match-id" [match-id]
                            (GET "/" []
                              (json/write-str (engine/get-game-state game-id match-id)))
                            (POST "/apply-move" request
                              (let [move (:body request)]
                                (json/write-str (engine/apply-move-to game-id match-id move))))))
                        (route/not-found "Not Found"))))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body {:keywords? true})
      middleware/wrap-json-response))
