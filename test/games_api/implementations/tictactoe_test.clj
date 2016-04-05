(ns games-api.implementations.tictactoe-test
  (:use games-api.protocols.game-protocol)
  (:require [clojure.test :refer :all])
  (:import (games_api.implementations.tictactoe TicTacToe)))

(deftest tictactoe-test
  (testing "TicTacToe details has all required fields"
    (let [ttt-game-details (game-details (TicTacToe.))]
      (is (get ttt-game-details "name"))
      (is (get ttt-game-details "desc"))
      (is (get ttt-game-details "moves"))))

  (testing "TicTacToe returns empty board as initial state"
    (is (= (initial-state (TicTacToe.)) (vec (take 9 (repeat "E"))))))

  (testing "TicTacToe must accept player 1 move when in the initial state"
    (let [ttt-instance (TicTacToe.)
          current-state (initial-state ttt-instance)
          response (apply-move ttt-instance current-state {
                                                           "move-id" "place-marker"
                                                           "player-id" 1
                                                           "marker-position" 5
                                                           })]
          (is (=  (get response "status") "ok"))
          (is (=  (get response "new-state")
                  (assoc (vec (take 9 (repeat "E"))) 4 "X"))))))

(testing "TicTacToe must accept player 2 move when in the initial state"
  (let [ttt-instance (TicTacToe.)
        current-state (initial-state ttt-instance)
        response (apply-move ttt-instance current-state {
                                                         "move-id" "place-marker"
                                                         "player-id" 2
                                                         "marker-position" 3
                                                         })]
    (is (=  (get response "status") "ok"))
    (is (=  (get response "new-state")
            (assoc (vec (take 9 (repeat "E"))) 2 "O")))))

  (testing "TicTacToe must not accept placing a marker over an existing one from player 1"
    (let [ttt-instance (TicTacToe.)
          current-state (assoc (vec (take 9 (repeat "E"))) 3 "X")]
          (let [response (apply-move ttt-instance current-state {
                                                            "move-id" "place-marker"
                                                            "player-id" 1
                                                            "marker-position" 4
                                                            })]
            (is (= (get response "status") "error")))))

  (testing "TicTacToe must not accept placing a marker over an existing one from player 2"
    (let [ttt-instance (TicTacToe.)
          current-state (assoc (vec (take 9 (repeat "E"))) 2 "O")]
          (let [response (apply-move ttt-instance current-state {
                                                            "move-id" "place-marker"
                                                            "player-id" 2
                                                            "marker-position" 3
                                                            })]
            (is (= (get response "status") "error")))))