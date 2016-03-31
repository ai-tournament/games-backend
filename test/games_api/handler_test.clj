(ns games-api.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [games-api.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/list-games"))]
      (is (= (:status response) 200))
      (is (= (:body response) "[
                  {
                    id: \"tictactoe\",
                    name: \"Tic Tac Toe\"
                  }]"))))

  (testing "specific game route")
  (let [response (app (mock/request :get "/tictactoe"))]
    (is (= (:status response) 200)))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
