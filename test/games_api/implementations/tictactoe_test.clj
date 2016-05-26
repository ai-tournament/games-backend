(ns games-api.implementations.tictactoe-test
  (:use games-api.protocols.game-protocol)
  (:require [clojure.test :refer :all])
  (:require [games-api.implementations.tictactoe :refer :all])
  (:import (games_api.implementations.tictactoe TicTacToe)))

(deftest tictactoe-test
  (testing "TicTacToe details has all required fields"
    (let [ttt-game-details (game-details (TicTacToe.))]
      (is (get ttt-game-details "name"))
      (is (get ttt-game-details "desc"))
      (is (get ttt-game-details "moves"))))

  (testing "TicTacToe returns empty board as initial state"
    (is (= (initial-state (TicTacToe.)) (vec (take 3 (repeat (vec (take 3 (repeat "E")))))))))

  (testing "TicTacToe must accept player 1 move when in the initial state"
    (let [ttt-instance (TicTacToe.)
          current-state (initial-state ttt-instance)
          response (apply-move ttt-instance current-state {
                                                           "move-id" "place-marker"
                                                           "player-id" 1
                                                           "marker-position" [0 0]
                                                           })]
          (is (=  (get response "status") "ok"))
          (is (=  (get response "new-state")
                  [["X" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]))))

  (testing "TicTacToe must accept player 2 move when in the initial state"
    (let [ttt-instance (TicTacToe.)
        current-state (initial-state ttt-instance)
        response (apply-move ttt-instance current-state {
                                                         "move-id" "place-marker"
                                                         "player-id" 2
                                                         "marker-position" [2 2]
                                                         })]
    (is (=  (get response "status") "ok"))
    (is (=  (get response "new-state")
            [["E" "E" "E"] ["E" "E" "E"] ["E" "E" "O"]]))))

  (testing "TicTacToe must not accept placing a marker over an existing one from player 1"
    (let [ttt-instance (TicTacToe.)
          current-state [["X" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]]
          (let [response (apply-move ttt-instance current-state {
                                                            "move-id" "place-marker"
                                                            "player-id" 1
                                                            "marker-position" [0 0]
                                                            })]
            (is (= (get response "status") "error"))
            (is (= (get response "new-state") current-state)))))

  (testing "TicTacToe must not accept placing a marker over an existing one from player 2"
    (let [ttt-instance (TicTacToe.)
          current-state [["E" "E" "E"] ["E" "E" "E"] ["E" "E" "O"]]]
          (let [response (apply-move ttt-instance current-state {
                                                            "move-id" "place-marker"
                                                            "player-id" 2
                                                            "marker-position" [2 2]
                                                            })]
            (is (= (get response "status") "error"))
            (is (= (get response "new-state") current-state)))))

  (testing "TicTacToe should consider the game finished when there are no empty places in the board"
    (let [ttt-instance (TicTacToe.)]
      (is (finished ttt-instance [["O" "X" "O"] ["X" "O" "X"] ["O" "X" "O"]]))))

  (testing "TicTacToe should consider the game finished when there is a row filled with the same marker"
    (let [ttt-instance (TicTacToe.)]
      (let [result (finished ttt-instance [["X" "X" "X"] ["E" "E" "E"] ["E" "E" "E"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 1)))
      (let [result (finished ttt-instance [["E" "E" "E"] ["O" "O" "O"] ["E" "E" "E"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 2)))
      (let [result (finished ttt-instance [["E" "E" "E"] ["E" "E" "E"] ["X" "X" "X"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 1)))))

  (testing "TicTacToe should consider the game finished when there is a column filled with the same marker"
    (let [ttt-instance (TicTacToe.)]
      (let [result (finished ttt-instance [["X" "E" "E"] ["X" "E" "E"] ["X" "E" "E"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 1)))
      (let [result (finished ttt-instance [["E" "O" "E"] ["E" "O" "E"] ["E" "O" "E"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 2)))
      (let [result (finished ttt-instance [["E" "E" "X"] ["E" "E" "X"] ["E" "E" "X"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 1)))))

  (testing "TicTacToe should consider the game finished when there is a diagonal filled with the same marker"
    (let [ttt-instance (TicTacToe.)]
      (let [result (finished ttt-instance [["X" "E" "E"] ["E" "X" "E"] ["E" "E" "X"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 1)))
      (let [result (finished ttt-instance [["E" "E" "O"] ["E" "O" "E"] ["O" "E" "E"]])]
        (is (get result "finished"))
        (is (= (get result "winner-id") 2)))))

  (testing "TicTacToe should not accept new moves if the game is finished"
    (let [ttt-instance (TicTacToe.)
          current-state [["E" "E" "X"] ["E" "E" "X"] ["E" "E" "X"]]]
      (let [response (apply-move ttt-instance current-state {
                                                       "move-id" "place-marker"
                                                       "player-id" 1
                                                       "marker-position" [0 0]
                                                       })]
        (is (= (get response "status") "error"))
        (is (= (get response "new-state") current-state)))))

  (testing "TicTacToe should set the position of the marker"
    (let [current-state [["E" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]
          expected-state [["X" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]
          pos [0 0]
          marker "X"
          response (set-position-value current-state pos marker)
          ]

      (is (= response expected-state))
      )
    )
  )
