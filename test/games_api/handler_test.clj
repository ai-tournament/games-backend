(ns games-api.handler-test
  (:use games-api.repositories.game-repository)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [games-api.handler :refer :all]
            [clojure.data.json :as json]))

; TODO replace the expected response with the ones from the database
(deftest test-app
  (testing "games list route"
    (let [expected-response [{:game-id "tictactoe" :game-name "Tic-Tac-Toe"}
                             {:game-id "chess" :game-name "Chess"}]
          response (app (mock/request :get "/games"))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/write-str expected-response)))))

  (testing "get game details by game id"
    (let [expected-response {:name  "Tic Tac Toe game!"
                             :desc  "It's really cool :)"
                             :moves {}}
          response (app (mock/request :get "/games/tictactoe"))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/write-str expected-response))))

    (let [expected-response "Awesome chess game!"
          response (app (mock/request :get "/games/chess"))]
      (is (= (:status response) 200))
      (is (= (:body response) (json/write-str expected-response)))))

  (testing "get a new match state"
    (let [expected-response [["E" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]
          response (app (mock/request :get (str "/games/tictactoe/" (int (rand 1000000)))))]
      (is (= (:body response) (json/write-str expected-response)))))

  (testing "apply a move"
    (let [expected-response {:is-valid true :new-state [["E" "E" "E"] ["E" "X" "E"] ["E" "E" "E"]]}
          response (app (mock/content-type
                          (mock/body
                            (mock/request :post (str "/games/tictactoe/" (int (rand 1000000)) "/apply-move"))
                            (json/write-str {:move-id         "place-marker"
                             :player-id       0
                             :marker-position {:x 1 :y 1}}))
                          "application/json"))]
      (is (= (:body response) (json/write-str expected-response))))))

