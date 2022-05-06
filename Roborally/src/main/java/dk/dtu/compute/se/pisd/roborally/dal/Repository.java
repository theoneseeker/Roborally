/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Components.EnergyCube;
import dk.dtu.compute.se.pisd.roborally.model.Components.Upgrade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart
 *
 */
class Repository implements IRepository {

    private static final String GAME_GAMEID = "gameID";

    private static final String GAME_NAME = "name";

    private static final String GAME_CURRENTPLAYER = "currentPlayer";

    private static final String GAME_PHASE = "phase";

    private static final String GAME_STEP = "step";

    private static final String PLAYER_PLAYERID = "playerID";

    private static final String PLAYER_NAME = "name";

    private static final String PLAYER_COLOUR = "colour";

    private static final String PLAYER_GAMEID = "gameID";

    private static final String PLAYER_POSITION_X = "positionX";

    private static final String PLAYER_POSITION_Y = "positionY";

    private static final String PLAYER_HEADING = "heading";

    private static final String CHECKPOINT = "checkpoint";

    private static final String ENERGY = "energy";

    private Connector connector;

    Repository(Connector connector){
        this.connector = connector;
    }

    /**
     * Creates a new game in the database.
     * @author s205444, Lucas
     * @param game game board used to save details about the board such as name in the database.
     * @return true if game is saved successfully.
     */

    @Override
    public boolean createGameInDB(Board game) {
        if (game.getGameId() == null) {
            Connection connection = connector.getConnection();
            try {
                connection.setAutoCommit(false);

                PreparedStatement ps = getInsertGameStatementRGK();
                ps.setString(1, game.getName()); // instead of name
                ps.setNull(2, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
                ps.setInt(3, game.getPhase().ordinal());
                ps.setInt(4, game.getStep());
                ps.setString(5, game.getBoardName());

                // If you have a foreign key constraint for current players,
                // the check would need to be temporarily disabled, since
                // MySQL does not have a per transaction validation, but
                // validates on a per row basis.
                // Statement statement = connection.createStatement();
                // statement.execute("SET foreign_key_checks = 0");

                int affectedRows = ps.executeUpdate();
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (affectedRows == 1 && generatedKeys.next()) {
                    game.setGameId(generatedKeys.getInt(1));
                }
                generatedKeys.close();

                // Enable foreign key constraint check again:
                // statement.execute("SET foreign_key_checks = 1");
                // statement.close();

                createPlayersInDB(game);
                createCardRegisterInDB(game);
                createSpacesInDB(game);

                // since current player is a foreign key, it can oly be
                // inserted after the players are created, since MySQL does
                // not have a per transaction validation, but validates on
                // a per row basis.
                ps = getSelectGameStatementU();
                ps.setInt(1, game.getGameId());

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
                    rs.updateRow();
                }
                rs.close();

                connection.commit();
                connection.setAutoCommit(true);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Could not create game in database");

                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException e1) {
                    System.err.println("Failed to rollback after failing to create game in database.");
                    e1.printStackTrace();
                }
            }
        } else {
            System.err.println("Game cannot be created in DB, since it has a game id already!");
        }
        return false;
    }

    /**
     * Updates a game in the database.
     * @author s205444, Lucas
     * @param game Object of type Game that is currently being played with.
     * @return true if updated successfully.
     */
    @Override
    public boolean updateGameInDB(Board game) {
        assert game.getGameId() != null;

        Connection connection = connector.getConnection();
        try {
            connection.setAutoCommit(false);

            PreparedStatement ps = getSelectGameStatementU();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
                rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
                rs.updateInt(GAME_STEP, game.getStep());
                rs.updateRow();
            }
            rs.close();
            updateCardsInDB(game);
            updatePlayersInDB(game);
            updateSpaceInDB(game);

            connection.commit();
            connection.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Some DB error");

            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException e1) {
                System.out.print("Failed rollback");
                e1.printStackTrace();
            }
        }

        return false;
    }

    /**
     * Loads a game from the database.
     * @author s205444, Lucas
     * @param id chosen id amongst the games in the database already.
     * @return returns the current gamestate from the database if no errors occured. Otherwise, returns NULL.
     */
    @Override
    public Board loadGameFromDB(int id) {
        Board game;
        try {
            PreparedStatement ps = getSelectGameStatementU();
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            int playerNo;
            if (rs.next()) {
                String boardName = rs.getString("board");
                game = LoadBoard.loadBoard(boardName);
                if (game == null) {
                    return null;
                }
                playerNo = rs.getInt(GAME_CURRENTPLAYER);

                game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
                game.setStep(rs.getInt(GAME_STEP));
            } else {
                System.out.println("Error: database is empty");
                return null;
            }
            rs.close();

            game.setGameId(id);
            loadPlayersFromDB(game);
            loadCards(game);
            loadSpacesFromDB(game);

            if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
                game.setCurrentPlayer();
            } else {
                System.out.println("Error: Wrong game fetched from database");
                return null;
            }

            return game;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Some DB error - SQL Exception");
        }
        return null;
    }

    @Override
    public List<GameInDB> getGames() {
        List<GameInDB> result = new ArrayList<>();
        try {
            PreparedStatement ps = getSelectGameIdsStatement();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt(GAME_GAMEID);
                String name = rs.getString(GAME_NAME);
                result.add(new GameInDB(id,name));
            }
            rs.close();
        } catch (SQLException e) {
            System.out.println("Could not fetch games form database");
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * Creates players in the database.
     * @author s205444, Lucas
     * @param game the current board played on.
     * @throws SQLException throws to caller
     */

    private void createPlayersInDB(Board game) throws SQLException {
        try {
            PreparedStatement ps = getSelectPlayersStatementU();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            for (int i = 0; i < game.getPlayersNumber(); i++) {
                Player player = game.getPlayer(i);
                rs.moveToInsertRow();
                rs.updateInt(PLAYER_GAMEID, game.getGameId());
                rs.updateInt(PLAYER_PLAYERID, i);
                rs.updateString(PLAYER_NAME, player.getName());
                rs.updateString(PLAYER_COLOUR, player.getColor());
                rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
                rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
                rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
                rs.updateInt(CHECKPOINT, player.getCheckpoints());
                rs.updateInt(ENERGY, player.getEnergy());
                int k = 10;
                if(!player.getUpgradeList().isEmpty()){
                    for(Upgrade upgrade : player.getUpgradeList()){
                        rs.updateString(k,upgrade.toString());
                        k++;
                    }
                }

                rs.insertRow();
            }

            rs.close();
        }
        catch(SQLException e){
            System.err.print("Could not save game. An error occurred.");
            throw new SQLException();
        }
    }

    /**
     * @author s205444, Lucas
     * Creates spaces in the database. This is used to save spaces that may chagne. For now, just energy cubes will change.
     * @param game the current board being played on.
     * @throws SQLException throws to caller
     */
    private void createSpacesInDB(Board game) throws SQLException {
        try {
            PreparedStatement ps = getSelectSpaceSTMT();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            for (int i = 0; i < game.width; i++) {
                for(int k = 0; k < game.height; k++) {
                    Space space = game.getSpace(i,k);
                    int energy = 0;
                    for(FieldAction fa : space.getActions()) {
                        if(fa instanceof EnergyCube) {
                            EnergyCube energyCube = (EnergyCube) fa;
                            energy = energyCube.getEnergy();
                        }
                    }
                    rs.moveToInsertRow();
                    rs.updateInt(PLAYER_GAMEID, game.getGameId());
                    rs.updateInt("x", i);
                    rs.updateInt("y", k);
                    rs.updateInt("energy", energy );

                    rs.insertRow();
                }
            }

            rs.close();
        }
        catch(SQLException e){
            System.out.print("Could not save game. An error occurred.");
            throw new SQLException();
        }
    }

    /**
     * @author s205444, Lucas
     * Creates data entries for playerRegisters and playerHands in the database.
     * @param game the current board being palyed on.
     * @throws SQLException throws to caller
     */
    private void createCardRegisterInDB(Board game) throws SQLException{
        try {
            PreparedStatement ps = getSelectPlayerHandStatement();
            ps.setInt(1, game.getGameId());
            ResultSet rs = ps.executeQuery();


            for (int i = 0; i < game.getPlayersNumber(); i++) {
                for (int j = 0; j < 8; j++) {
                    rs.moveToInsertRow();
                    rs.updateInt(PLAYER_GAMEID, game.getGameId());
                    rs.updateInt(PLAYER_PLAYERID, i);
                    rs.updateInt("Number", j);
                    CommandCard commandCard = game.getPlayer(i).getCardField(j).getCard();
                    if (commandCard != null) {
                        rs.updateInt("Ordinal", commandCard.command.ordinal());
                    } else {
                        rs.updateInt("Ordinal", -5);
                    }
                    rs.insertRow();
                }
            }
            rs.close();

            PreparedStatement ps2 = getSelectPlayerRegisterStatement();
            ps2.setInt(1, game.getGameId());
            ResultSet rs2 = ps2.executeQuery();

            for (int n = 0; n < game.getPlayersNumber(); n++) {
                for (int k = 0; k < 5; k++) {
                    rs2.moveToInsertRow();
                    rs2.updateInt(PLAYER_GAMEID, game.getGameId());
                    rs2.updateInt(PLAYER_PLAYERID, n);
                    rs2.updateInt("RegNumber", k);

                    CommandCard commandCard = game.getPlayer(n).getProgramField(k).getCard();
                    if (commandCard != null) {
                        rs2.updateInt("Ordinal", commandCard.command.ordinal());
                    } else {
                        rs2.updateInt("Ordinal", -99);
                    }
                    rs2.insertRow();
                }

            }
            rs2.close();
        }
        catch(SQLException e){
            System.err.println("Failed to create cards in database");
            throw e;
        }
    }

    /**
     * @author s205444, Lucas
     * Loads players from the database
     * @param game the current board played with
     * @throws SQLException throws to caller
     */
    private void loadPlayersFromDB(Board game) throws SQLException {
        try {
            PreparedStatement ps = getSelectPlayersASCStatement();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            int i = 0;
            while (rs.next()) {
                int playerId = rs.getInt(PLAYER_PLAYERID);
                if (i++ == playerId) {
                    String name = rs.getString(PLAYER_NAME);
                    String colour = rs.getString(PLAYER_COLOUR);
                    Player player = new Player(game, colour, name);
                    game.addPlayer(player);

                    int x = rs.getInt(PLAYER_POSITION_X);
                    int y = rs.getInt(PLAYER_POSITION_Y);
                    player.setSpace(game.getSpace(x, y));
                    int heading = rs.getInt(PLAYER_HEADING);
                    player.setHeading(Heading.values()[heading]);
                    int checkpoint = rs.getInt(CHECKPOINT);
                    int energy = rs.getInt(ENERGY);
                    player.setCheckpoints(checkpoint);
                    player.setEnergy(energy);
                    String upg1 = rs.getString("upgrade1");
                    String upg2 = rs.getString("upgrade2");
                    String upg3 = rs.getString("upgrade3");
                    for (int l = 0; l < 3; l++) {
                        for (Upgrade upgrade : Upgrade.values()) {
                            if (l == 0 && upg1 != null) {
                                if (upg1.equals(upgrade.toString()))
                                    player.addUpgrade(upgrade);
                            } else if (l == 1 && upg2 != null) {
                                if (upg2.equals(upgrade.toString()))
                                    player.addUpgrade(upgrade);
                            } else if (l == 2 && upg3 != null) {
                                if (upg3.equals(upgrade.toString()))
                                    player.addUpgrade(upgrade);
                            }
                        }
                    }
                } else {
                    System.err.println("Game in DB does not have a player with id " + i + "!");
                }
            }
            rs.close();
        }
        catch(SQLException e){
            System.err.println("Failed to LoadPlayers from DB");
            throw e;
        }
    }

    /**
     * Loads spaces from the databases. For now, we just use it for energy cubes.
     * @author s205444, Lucas
     * @param game the current board being played with.
     * @throws SQLException throws to caller
     */
    private void loadSpacesFromDB(Board game) throws SQLException {
        try {
            PreparedStatement ps = getSelectSpaceSTMT();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int x = rs.getInt("x");
                int y = rs.getInt("y");
                int energy = rs.getInt("energy");
                Space space = game.getSpace(x, y);
                for (FieldAction fa : space.getActions()) {
                    if (fa instanceof EnergyCube) {
                        EnergyCube energyCube = (EnergyCube) fa;
                        energyCube.setEnergy(energy);
                    }
                }
            }
            rs.close();
        }
        catch(SQLException e){
            System.err.println("failed to load Spaces from DB");
            throw e;
        }
    }

    /**
     * Loads cards into registers and hands.
     * @author s205444, Lucas
     * @param game the current board being played on.
     * @throws SQLException throws to caller
     */
    private void loadCards(Board game) throws SQLException{
        try {
            PreparedStatement ps = getSelectPlayerRegisterStatement();
            ps.setInt(1, game.getGameId());
            ResultSet rs = ps.executeQuery();

            Command[] cArray = Command.values();
            while (rs.next()) {
                int ID = rs.getInt(PLAYER_PLAYERID);
                int cardOrdinal = rs.getInt("Ordinal");
                int number = rs.getInt("RegNumber");
                if (cardOrdinal >= 0) {
                    game.getPlayer(ID).getProgramField(number).setCard(new CommandCard(cArray[cardOrdinal]));
                }
                if (cardOrdinal > 7) {
                    Player player = game.getPlayer(ID);
                    player.getDamagecards().add(cArray[cardOrdinal]);
                }
            }
            rs.close();
            PreparedStatement ps2 = getSelectPlayerHandStatement();
            ps2.setInt(1, game.getGameId());
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next()) {
                int ID = rs2.getInt(PLAYER_PLAYERID);
                int cardOrdinal = rs2.getInt("Ordinal");
                int number = rs2.getInt("Number");
                if (cardOrdinal >= 0) {
                    game.getPlayer(ID).getCardField(number).setCard(new CommandCard(cArray[cardOrdinal]));
                }
            }
            rs2.close();
        }
        catch(SQLException e){
            System.err.println("Failed to load cards from DB");
            throw e;
        }
    }

    /**
     * Updates cards in registers and player hands in database.
     * @author s205444, Lucas
     * @param game the current board played with.
     * @throws SQLException throws to caller
     */
    private void updateCardsInDB(Board game) throws SQLException{

        try {
            PreparedStatement ps1 = getSelectPlayerHandStatement();
            ps1.setInt(1, game.getGameId());


            ResultSet rs1 = ps1.executeQuery();
            int m = 0;
                while (rs1.next()) {
                    int playerId = rs1.getInt(PLAYER_PLAYERID);
                    Player player = game.getPlayer(playerId);
                    if(m == 8){
                        m = 0;
                    }
                        //rs1.updateInt("Number", m);
                        CommandCard cmdCard = player.getCardField(m).getCard();
                        if (cmdCard != null) {
                            rs1.updateInt("Ordinal", player.getCardField(m).getCard().command.ordinal());
                        } else {
                            rs1.updateInt("Ordinal", -99);
                        }
                    rs1.updateRow();
                        m++;
                }

            rs1.close();

            PreparedStatement ps2 = getSelectPlayerRegisterStatement();
            ps2.setInt(1, game.getGameId());

            ResultSet rs2 = ps2.executeQuery();
            m = 0;
                while (rs2.next()) {
                    int playerId = rs2.getInt(PLAYER_PLAYERID);
                    Player player = game.getPlayer(playerId);
                    if(m == 5){
                        m = 0;
                    }
                        rs2.moveToCurrentRow();
                        //rs2.updateInt("RegNumber", m);
                        CommandCardField cmdCard = player.getProgramField(m);
                        if (cmdCard.getCard() != null) {
                            rs2.updateInt("Ordinal", player.getProgramField(m).getCard().command.ordinal());
                        } else {
                            rs2.updateInt("Ordinal", -99);
                        }

                    rs2.updateRow();
                        m++;
                }

            rs2.close();
        }
        catch(SQLException e){
            System.err.println("Failed to update cards in DB");
            throw e;
        }

    }

    private void updatePlayersInDB(Board game) throws SQLException {
        try {
            PreparedStatement ps = getSelectPlayersStatementU();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int playerId = rs.getInt(PLAYER_PLAYERID);
                Player player = game.getPlayer(playerId);
                // rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
                rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
                rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
                rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
                rs.updateInt("checkpoint", player.getCheckpoints());
                rs.updateInt("energy", player.getEnergy());
                int k = 10;
                if(!player.getUpgradeList().isEmpty()){
                    for(Upgrade upgrade : player.getUpgradeList()){
                        rs.updateString(k,upgrade.toString());
                        k++;
                    }
                }
                rs.updateRow();
            }
            rs.close();
        }
        catch (SQLException e){
            System.out.print("Could not update players in database");
            throw e;
        }
    }

    /**
     * Updates spaces in the database
     * @author s205444 Lucas
     * @param game the board being played with
     * @throws SQLException throws exception to caller.
     */
    private void updateSpaceInDB(Board game) throws SQLException {
        try {
            PreparedStatement ps = getSelectSpaceSTMT();
            ps.setInt(1, game.getGameId());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int x = rs.getInt("x");
                int y = rs.getInt("y");
                Space space = game.getSpace(x,y);
                for(FieldAction fa : space.getActions()){
                    if(fa instanceof EnergyCube){
                        rs.updateInt(ENERGY, ((EnergyCube) fa).getEnergy());
                    }
                }
                rs.updateRow();
            }
            rs.close();
        }
        catch (SQLException e){
            System.out.println("Could not update Spaces in database");
            throw e;
        }
    }

    private static final String SQL_INSERT_GAME =
            "INSERT INTO Game(name, currentPlayer, phase, step, board) VALUES (?, ?, ?, ?, ?)";

    private PreparedStatement insert_game_stmt = null;

    private PreparedStatement getInsertGameStatementRGK() {
        if (insert_game_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                insert_game_stmt = connection.prepareStatement(
                        SQL_INSERT_GAME,
                        Statement.RETURN_GENERATED_KEYS);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return insert_game_stmt;
    }

    private static final String SQL_SELECT_GAME =
            "SELECT * FROM Game WHERE gameID = ?";

    private PreparedStatement select_game_stmt = null;

    private PreparedStatement getSelectGameStatementU() {
        if (select_game_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_game_stmt = connection.prepareStatement(
                        SQL_SELECT_GAME,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                System.err.println("Could not create connection with SelectGameStatement in database.");
            }
        }
        return select_game_stmt;
    }

    private static final String SQL_SELECT_PLAYERS =
            "SELECT * FROM Player WHERE gameID = ?";

    private PreparedStatement select_players_stmt = null;

    private PreparedStatement getSelectPlayersStatementU() {
        if (select_players_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_players_stmt = connection.prepareStatement(
                        SQL_SELECT_PLAYERS,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                System.err.println("Could not create connection with SelectPlayerStatement in database.");
            }
        }
        return select_players_stmt;
    }


    private static final String SQL_SELECT_PLAYER_HAND =
            "SELECT * FROM PlayerHand WHERE gameID = ?";

    private PreparedStatement select_player_hand_stmt = null;

    private PreparedStatement getSelectPlayerHandStatement() {
        if (select_player_hand_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_player_hand_stmt = connection.prepareStatement(SQL_SELECT_PLAYER_HAND,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                System.err.println("Could not create connection with SelectPlayerHand statement in database.");
                e.printStackTrace();
            }
        }
        return select_player_hand_stmt;
    }

    private static final String SQL_SELECT_PLAYER_REGISTER =
            "SELECT * FROM PlayerRegister WHERE gameID = ?";

    private PreparedStatement select_player_register_stmt = null;

    private PreparedStatement getSelectPlayerRegisterStatement() {
        if (select_player_register_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_player_register_stmt = connection.prepareStatement(SQL_SELECT_PLAYER_REGISTER,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                System.err.println("Could not create connection with SQL_SELECT_PLAYER_REGISTER in database.");
                e.printStackTrace();
            }
        }
        return select_player_register_stmt;
    }


    private static final String SQL_SELECT_PLAYERS_ASC =
            "SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";

    private PreparedStatement select_players_asc_stmt = null;

    private PreparedStatement getSelectPlayersASCStatement() {
        if (select_players_asc_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                // This statement does not need to be updatable
                select_players_asc_stmt = connection.prepareStatement(
                        SQL_SELECT_PLAYERS_ASC);
            } catch (SQLException e) {
                System.err.println("Could not create connection with SQL_SELECT_PLAYER_REGISTER in database.");
                e.printStackTrace();
            }
        }
        return select_players_asc_stmt;
    }

    private static final String SQL_SELECT_GAMES =
            "SELECT gameID, name FROM Game";

    private PreparedStatement select_games_stmt = null;

    private PreparedStatement getSelectGameIdsStatement() {
        if (select_games_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_games_stmt = connection.prepareStatement(
                        SQL_SELECT_GAMES);
            } catch (SQLException e) {
                System.err.println("Could not create connection with SQL_SELECT_PLAYER_REGISTER in database.");
                e.printStackTrace();
            }
        }
        return select_games_stmt;
    }

    private static final String SQL_SELECT_SPACE =
            "SELECT * FROM Spaces WHERE gameID = ?";

    private PreparedStatement select_space_stmt = null;

    private PreparedStatement getSelectSpaceSTMT() {
        if (select_space_stmt == null) {
            Connection connection = connector.getConnection();
            try {
                select_space_stmt = connection.prepareStatement(
                        SQL_SELECT_SPACE,
                        ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
            } catch (SQLException e) {
                System.err.println("Could not connect to database.");
                e.printStackTrace();
            }
        }
        return select_space_stmt;
    }
}