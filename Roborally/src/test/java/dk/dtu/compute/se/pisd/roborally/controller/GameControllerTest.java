package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        board.getSpace(0, 0).setAntenna(true);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.SOUTH);
        }
        board.setCurrentPlayer();

    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }


    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void fastForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.fastForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");

    }

    @Test
    void tripleForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.tripleForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 3).getPlayer(), "Player " + current.getName() + " should beSpace (0,3)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");

    }

    @Test
    void backUp() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.backUp(current);

        Assertions.assertEquals(current, board.getSpace(0, 7).getPlayer(), "Player " + current.getName() + " should beSpace (0,7)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void turnRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnRight(current);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should beSpace (0,0)!");
        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player 0 should be heading EAST!");
        Assertions.assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should NOT be empty!");
    }

    @Test
    void turnLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.turnLeft(current);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should beSpace (0,0)!");
        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player 0 should be heading WEST!");
        Assertions.assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should NOT be empty!");


    }

    @Test
    void uTurn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uTurn(current);

        Assertions.assertEquals(current, board.getSpace(0, 0).getPlayer(), "Player " + current.getName() + " should beSpace (0,0)!");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player 0 should be heading NORTH!");
        Assertions.assertNotNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    @Test
    void push() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);
        Player player3 = board.getPlayer(2);
        player1.setSpace(board.getSpace(0,0));
        player2.setSpace(board.getSpace(0,1));
        player3.setSpace(board.getSpace(0,2));


        Assertions.assertEquals(player1, board.getSpace(0, 1).getPlayer(), "Player 1 " + player1.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(player2, board.getSpace(0, 2).getPlayer(), "Player 2 " + player2.getName() + " should beSpace (0,2)!");
        Assertions.assertEquals(player3, board.getSpace(0, 3).getPlayer(), "Player 3 " + player3.getName() + " should beSpace (0,3)!");
        Assertions.assertEquals(Heading.SOUTH, player1.getHeading(), "Player 1 should be heading SOUTH!");
        Assertions.assertEquals(Heading.SOUTH, player2.getHeading(), "Player 2 should be heading SOUTH!");
        Assertions.assertEquals(Heading.SOUTH, player3.getHeading(), "Player 3 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }
}