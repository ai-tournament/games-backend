(ns games-api.handler-test
  (:use games-api.daos.games-dao)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [games-api.handler :refer :all]
            [clojure.data.json :as json]))

; TODO replace the expected response with the ones from the database
(deftest test-app
  (testing "games list route"
    (def expected-response  [{:game-id "chess" :game-name "Chess"}])
      (let [response (app (mock/request :get "/games"))]
        (is (= (:status response) 200))
        (is (= (:body response) (json/write-str expected-response))))))

