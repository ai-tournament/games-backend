(ns games-api.implementations.tictactoe-test
  (:use games-api.protocols.game-protocol)
  (:require [clojure.test :refer :all])
  (:require [games-api.implementations.tictactoe :refer :all])
  (:import (games_api.implementations.tictactoe TicTacToe)))

(deftest tictactoe-test
  (testing "TicTacToe details has all required fields"
    (let [ttt-game-details (game-details (TicTacToe.))]
      (is (:name ttt-game-details))
      (is (:desc ttt-game-details))
      (is (:moves ttt-game-details))))

  (testing "TicTacToe returns empty board as initial state"
    (is (= (initial-state (TicTacToe.)) (vec (take 3 (repeat (vec (take 3 (repeat "E")))))))))

  (testing "TicTacToe must accept player 1 move when in the initial state"
    (let [ttt-instance (TicTacToe.)
          current-state (initial-state ttt-instance)
          response (apply-move ttt-instance current-state {:move-id         "place-marker"
                                                           :player-id       0
                                                           :marker-position {:x 0 :y 0}})]
      (is (true? (:is-valid response)))
      (is (= (:new-state response) [["X" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]))))

  (testing "TicTacToe must accept player 2 move when in the initial state"
    (let [ttt-instance (TicTacToe.)
          current-state (initial-state ttt-instance)
          response (apply-move ttt-instance current-state {:move-id         "place-marker"
                                                           :player-id       1
                                                           :marker-position {:x 2 :y 2}})]
      (is (:is-valid response))
      (is (= (:new-state response) [["E" "E" "E"] ["E" "E" "E"] ["E" "E" "O"]]))))

  (testing "TicTacToe must not accept placing a marker over an existing one from player 1"
    (let [ttt-instance (TicTacToe.)
          current-state [["X" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]]
      (let [response (apply-move ttt-instance current-state {:move-id         "place-marker"
                                                             :player-id       0
                                                             :marker-position {:x 0 :y 0}})]
        (is (not (:is-valid response)))
        (is (= (:new-state response) current-state)))))

  (testing "TicTacToe must not accept placing a marker over an existing one from player 2"
    (let [ttt-instance (TicTacToe.)
          current-state [["E" "E" "E"] ["E" "E" "E"] ["E" "E" "O"]]]
      (let [response (apply-move ttt-instance current-state {:move-id         "place-marker"
                                                             :player-id       1
                                                             :marker-position {:x 2 :y 2}})]
        (is (not (:is-valid response)))
        (is (= (:new-state response) current-state)))))

  (testing "TicTacToe should consider the game finished when there are no empty places in the board"
    (let [ttt-instance (TicTacToe.)]
      (let [game-state [["O" "X" "O"] ["X" "O" "X"] ["O" "X" "O"]]]
      (is (is-finished? ttt-instance game-state)))))

  (defn setup-test
    [game-instance game-state]
    [(is-finished? game-instance game-state)
     (get-winner game-instance game-state)])

  (testing "TicTacToe should consider the game finished when there is a row filled with the same marker"
    (let [ttt-instance (TicTacToe.)]
      (let [[finished winner] (setup-test ttt-instance [["X" "X" "X"] ["E" "E" "E"] ["E" "E" "E"]])]
        (is finished)
        (is (= winner 0)))
      (let [[finished winner] (setup-test ttt-instance [["E" "E" "E"] ["O" "O" "O"] ["E" "E" "E"]])]
        (is finished)
        (is (= winner 1)))
      (let [[finished winner] (setup-test ttt-instance [["E" "E" "E"] ["E" "E" "E"] ["X" "X" "X"]])]
        (is finished)
        (is (= winner 0)))))

  (testing "TicTacToe should consider the game finished when there is a column filled with the same marker"
    (let [ttt-instance (TicTacToe.)]
      (let [[finished winner] (setup-test ttt-instance [["X" "E" "E"] ["X" "E" "E"] ["X" "E" "E"]])]
        (is finished)
        (is (= winner 0)))
      (let [[finished winner] (setup-test ttt-instance [["E" "O" "E"] ["E" "O" "E"] ["X" "O" "X"]])]
        (is finished)
        (is (= winner 1)))
      (let [[finished winner] (setup-test ttt-instance [["E" "E" "X"] ["E" "E" "X"] ["E" "E" "X"]])]
        (is finished)
        (is (= winner 0)))))

  (testing "TicTacToe should consider the game finished when there is a diagonal filled with the same marker"
    (let [ttt-instance (TicTacToe.)]
      (let [[finished winner] (setup-test ttt-instance [["X" "E" "E"] ["E" "X" "E"] ["E" "E" "X"]])]
        (is finished)
        (is (= winner 0)))
      (let [[finished winner] (setup-test ttt-instance [["E" "E" "O"] ["E" "O" "E"] ["O" "E" "E"]])]
        (is finished)
        (is (= winner 1)))))

  (testing "TicTacToe should not accept new moves if the game is finished"
    (let [ttt-instance (TicTacToe.)
          current-state [["E" "E" "X"] ["E" "E" "X"] ["E" "E" "X"]]]
      (let [response (apply-move ttt-instance current-state {:move-id         "place-marker"
                                                             :player-id       0
                                                             :marker-position {:x 0 :y 0}})]
        (is (not (:is-valid response)))
        (is (= (:new-state response) current-state)))))

  (testing "TicTacToe should set the position of the marker"
    (let [current-state [["E" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]
          expected-state [["X" "E" "E"] ["E" "E" "E"] ["E" "E" "E"]]
          pos {:x 0 :y 0}
          marker "X"
          response (set-position-value current-state pos marker)]
      (is (= response expected-state))
      )
    )
  )
