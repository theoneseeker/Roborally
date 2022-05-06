package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.dal.GameInDB;
import dk.dtu.compute.se.pisd.roborally.dal.RepositoryAccess;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * ...
 *
 * @author s205444, Lucas Loft Skals
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * @author s205444, Lucas
     * Presents two choice dialog options when starting a new game. Here, a pop up window
     * shows asking for player number and the board you wish to play on.
     */

    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    saveGame();
                    return;
                }
            }
            Board board = null;
            try {
                String RESSOURCEFOLDER = "src/main/resources/boards";
                File f = new File(RESSOURCEFOLDER);
                String[] boardNames = f.list();
                for (int i = 0; i < boardNames.length; i++) {
                    boardNames[i] = boardNames[i].substring(0, boardNames[i].length() - 5);
                }


                ChoiceDialog<String> boarddialog = new ChoiceDialog<>(boardNames[0], boardNames);
                boarddialog.setTitle("Choose board");
                boarddialog.setContentText("List of boards:");
                boarddialog.setHeaderText("Please, choose a board from the list below.");
                Optional<String> boardChosen = boarddialog.showAndWait();

                if (boardChosen.isPresent()) {
                    String boardConcave = boardChosen.get();
                    board = LoadBoard.loadBoard(boardConcave);
                    gameController = new GameController(board); //replace board parameter with loadBoard(DEFAULTBOARD)
                }
            }
            catch (Exception e) {
                System.err.print("Could not find boards in ressource folder");
                board = LoadBoard.loadBoard(null);
                gameController = new GameController(board);
            }
            if(board == null){
                System.err.print("Could not find any boards in ressource folder. Now exiting");
                return;
            }
                int no = result.get();
                for (int i = 0; i < no; i++) {
                    Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                    board.addPlayer(player);
                    player.setSpace(board.getSpace(i % board.width, i));
                }
                int k = 0;
                for (int i = 0; i < board.width; i++) {
                    for (int o = 0; o < board.height; o++) {
                        if (board.getSpace(i, o).getStartPoint()) {
                            board.getPlayer(k++).setSpace(board.getSpace(i, o));
                            if (k == board.getPlayersNumber()) {
                                break;
                            }
                        }
                    }
                    if (k == board.getPlayersNumber()) {
                        break;
                    }
                }

                // XXX: V2
                board.setCurrentPlayer();
                gameController.startProgrammingPhase();

                roboRally.createBoardView(gameController);
            }
        }




    /**
     * @author s205444, Lucas
     * Here, a game is saved or updated, if it was previously saved, the game is just updated. Otherwise, it prompts the user
     * for a text input to save the game.
     */
    public void saveGame() {
        try {
            boolean savedGame = false;

            List<GameInDB> gameIDs = RepositoryAccess.getRepository().getGames();

            for (GameInDB gameID : gameIDs) {
                if (gameController.board.getGameId() != null) {
                    if (gameID.id == gameController.board.getGameId()) {
                        savedGame = true;
                    }
                }
            }
            if (savedGame) {
                RepositoryAccess.getRepository().updateGameInDB(gameController.board);
            } else {
                TextInputDialog dialog = new TextInputDialog("");
                dialog.setTitle("Save Game");
                dialog.setHeaderText("If you wish to save, \n please type a name for your game:");
                dialog.setContentText("Enter name");


                Optional<String> result = dialog.showAndWait();

                String Realresult = dialog.getResult();
                if (result.isPresent() && !Realresult.equals("")) {
                    gameController.board.setName(Realresult);
                    RepositoryAccess.getRepository().createGameInDB(gameController.board);
                } else {
                    System.out.println("You cancelled the save operation");
                }
            }
        }
        catch (Exception e){
            System.err.println("Some issue saving the game.");
        }
    }

    /**
     * @author s205444, Lucas
     * loads a game from the specified database.
     */
    public void loadGame() {
        try {
            if (gameController == null) {
                GameInDB currentGame = null;
                List<GameInDB> gameIDs = RepositoryAccess.getRepository().getGames();
                List<String> gameName = new ArrayList<>();
                for (GameInDB game : gameIDs) {
                    gameName.add(game.name);
                }
                ChoiceDialog<String> dialog = new ChoiceDialog<>(gameName.get(0), gameName);
                dialog.setTitle("Load game");
                dialog.setHeaderText("Select a game");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    for (GameInDB game : gameIDs) {
                        if (game.name.equals(result.get())) {
                            currentGame = game;
                        }
                    }
                    gameController = new GameController(RepositoryAccess.getRepository().loadGameFromDB(currentGame.id));
                    roboRally.createBoardView(gameController);
                }
            }
        }
        catch(Exception e){
            System.err.println("Something went wrong loading the game");
            e.printStackTrace();
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Exits the Roborally app after being prompted with yes/no answer to exit the app.
     */

    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }


    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}

