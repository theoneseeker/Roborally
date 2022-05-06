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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Components.Upgrade;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final Space[] startingPoints = new Space[6]; //used for JSON objects.
    private final List<Player> playerOrder = new ArrayList<>();

    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private final List<Player> players = new ArrayList<>();

    public int getCheckpointCounter() {
        return checkpointCounter;
    }

    public void setCheckpointCounter(int checkpointCounter) {
        this.checkpointCounter += checkpointCounter;
    }

    private int checkpointCounter;

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    private int counter;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    public Board(int width, int height) {
        this(width, height, "defaultboard");
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }
    /**
     * Loops through all spaces to find antenna
     * @return Space where antenna is located
     * @author Oline s201010*/

    private Space getAntenna(){
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                if(spaces[x][y].getAntenna()){
                    return spaces[x][y];
                }
            }
        }
        return null;
    }

    /**
     * Calculates distance from each player to the antenna, to determine who gets priority to go first
     * @return distance from player to antenna
     * @param player the current player
     * @author Oline s201010*/

    private double calculateDistanceToAntenna(@NotNull Player player) {
        int distance;
        int x = player.getSpace().x; // the players x coordinate
        int y = player.getSpace().y; // the players y coordinate
        Space antenna = getAntenna(); // the space where antenna is located
        assert antenna != null;
        distance = Math.abs(antenna.x-x)+Math.abs(antenna.y-y);
        return distance;

        /* To test if priority method works */
        //Random rand = new Random();
        //return rand.nextInt(players.size());
    }

    /**
     * Maps players to a value (distance calculated in previous method)
     * Adds all pairs to a list (distancelist)
     * Sorts the list according to distance and adds the sorted list to player order
     *
     *@author Oline s201010 */

    private void calculatePlayerOrder(){
        /*
        Calculate player distance to Space antenna
        and populate/re-populate player order
         */
        java.util.List<Pair<Player, Integer>> distanceList = new java.util.ArrayList<>();
        for (Player player : players) {
            distanceList.add(new Pair(player, calculateDistanceToAntenna(player)));
        }
        distanceList.sort(Comparator.comparing(Pair::getValue));

        playerOrder.clear();
        for (Pair<Player, Integer> playerIntegerPair : distanceList) {
            playerOrder.add(playerIntegerPair.getKey());
        }
    }

    /** Current player is set according to player order
     * If the current player is the last in the player order the method
     * recalculates the player order and sets the current player to the first index
     * in the recalculated list
     * @author Oline s201010*/

    public void setCurrentPlayer() {
        if (playerOrder.isEmpty()){
            calculatePlayerOrder();
            current = playerOrder.get(0);
        }
        else if (playerOrder.indexOf(current) == playerOrder.size() - 1){
            // current player was last in player order
            calculatePlayerOrder();
            current = playerOrder.get(0);
        }
        else
        {
            current = playerOrder.get(playerOrder.indexOf(current) + 1);
        }
            notifyChange();
        }

    public List<Player> getPlayerOrder() {
        return playerOrder;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    public String getBoardName(){
        return boardName;
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH -> y = (y + 1);
            case WEST -> x = (x - 1);
            case NORTH -> y = (y - 1);
            case EAST -> x = (x + 1);
        }

        return getSpace(x, y);
    }

    public String getStatusMessage() {
        StringBuilder upgrades = new StringBuilder();
        for(Upgrade upgrade : getCurrentPlayer().getUpgradeList()){
            if(upgrade != null)
            upgrades.append("---- ").append(upgrade.displayName);
        }

   return "Player = " + getCurrentPlayer().getName() +  " | upgrades: " + upgrades + "---- Step: " + getStep() + " | Phase: " + getPhase().name();
}

}
