(ns games-api.handler
  (:import com.mchange.v2.c3p0.ComboPooledDataSource)
  (:require [games-api.implementations.tictactoe])
  (:import games_api.implementations.tictactoe.TicTacToe)
  (:require [games-api.implementations.chess])
  (:import games_api.implementations.chess.Chess)
  (:use compojure.core)
  (:use cheshire.core)
  (:use ring.util.response)
  (:use games-api.protocols.game-protocol)
  (:require [compojure.handler :as handler]
            [ring.middleware.json :as middleware]
            [clojure.java.jdbc :as sql]
            [compojure.route :as route]))

  (def db-config
    {:classname "org.h2.Driver"
     :subprotocol "h2"
     :subname "mem:documents"
     :user ""
     :password ""})

  (defn pool
    [config]
    (let [cpds (doto (ComboPooledDataSource.)
                 (.setDriverClass (:classname config))
                 (.setJdbcUrl (str "jdbc:" (:subprotocol config) ":" (:subname config)))
                 (.setUser (:user config))
                 (.setPassword (:password config))
                 (.setMaxPoolSize 6)
                 (.setMinPoolSize 1)
                 (.setInitialPoolSize 1))]
      {:datasource cpds}))

  (def pooled-db (delay (pool db-config)))

  (defn db-connection [] @pooled-db)

  (sql/with-connection (db-connection)
                       (sql/create-table :documents [:id "varchar(256)" "primary key"]
                                         [:title "varchar(1024)"]
                                         [:text :varchar]))

  (defn uuid [] (str (java.util.UUID/randomUUID)))

  (defn get-all-documents []
    (response
      (sql/with-connection (db-connection)
                           (sql/with-query-results results
                                                   ["select * from documents"]
                                                   (into [] results)))))

  (defn get-document [id]
    (sql/with-connection (db-connection)
                         (sql/with-query-results results
                                                 ["select * from documents where id = ?" id]
                                                 (cond
                                                   (empty? results) {:status 404}
                                                   :else (response (first results))))))

  (defn create-new-document [doc]
    (let [id (uuid)]
      (sql/with-connection (db-connection)
                           (let [document (assoc doc "id" id)]
                             (sql/insert-record :documents document)))
      (get-document id)))

  (defn update-document [id doc]
    (sql/with-connection (db-connection)
                         (let [document (assoc doc "id" id)]
                           (sql/update-values :documents ["id=?" id] document)))
    (get-document id))

  (defn delete-document [id]
    (sql/with-connection (db-connection)
                         (sql/delete-rows :documents ["id=?" id]))
    {:status 204})


  (def chess-creation "(Chess.)")
  (def games-instances
    {
     "tictactoe" (TicTacToe.)
     "chess"     (eval (read-string chess-creation))
     })

  (defroutes app-routes
             (GET "/" [] "Hello!")
             (context "/documents" [] (defroutes documents-routes
                                                 ;(GET "/state" [] (initial-state (IMPLS/->TicTacToe)))
                                                 (GET  "/" [] (get-all-documents))
                                                 (POST "/" {body :body} (create-new-document body))
                                                 (context "/:id" [id] (defroutes document-routes
                                                                                 (GET    "/" [] (get-document id))
                                                                                 (PUT    "/" {body :body} (update-document id body))
                                                                                 (DELETE "/" [] (delete-document id))))))

             (GET "/games/:game-id" [game-id] (game-details (get games-instances game-id)))
             (GET "/games/:game-id/initial-state" [game-id] (initial-state (get games-instances game-id)))
             (route/not-found "Not Found"))

  (def app
    (-> (handler/api app-routes)
        (middleware/wrap-json-body)
        middleware/wrap-json-response))
