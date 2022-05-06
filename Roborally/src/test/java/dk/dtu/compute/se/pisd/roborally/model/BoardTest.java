package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class BoardTest {

    private final int TEST_WIDTH = 10;
    private final int TEST_HEIGHT = 10;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT);
        board.getSpace(9, 9).setAntenna(true);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null, "Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            //player.setHeading(Heading.values()[i % Heading.values().length]);
            player.setHeading(Heading.SOUTH);
        }
        //board.setCurrentPlayer(board.getPlayer(0));

    }

    @AfterEach
    void tearDown() { gameController = null;}


    @Test
    void setCurrentPlayer() {
        Board board = gameController.board;
        Player current = board.getPlayer(0);

        board.setCurrentPlayer();

        Assertions.assertEquals(board.getPlayer(5), board.getCurrentPlayer(),
                "Player " + board.getCurrentPlayer().getName() + " should be current player according to priority");
    }
}