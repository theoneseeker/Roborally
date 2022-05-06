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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.Components.Laser;
import dk.dtu.compute.se.pisd.roborally.model.Components.Pit;
import dk.dtu.compute.se.pisd.roborally.model.Components.RebootTokens;
import dk.dtu.compute.se.pisd.roborally.model.Components.Upgrade;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public boolean winnerFound = false;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * @author Ekkart
     * Sets the current phase to programming phase. Sets the registers and playerhand ready to start programming.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                    if(!player.NeedReboot())
                        field.setCard2(null);

                    }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    //if statement to ensure that the player that is rebooting cant get any cards
                    if(!player.NeedReboot()) {
                        if (!player.getDamagecards().isEmpty()) {
                            if (player.getDamagecards().size() > j)
                                field.setCard(new CommandCard(player.getDamagecards().get(j)));
                            else
                                field.setCard(generateRandomCommandCard());
                        }
                        else
                            field.setCard(generateRandomCommandCard());
                    }
                    else
                        field.setCard(null);
                    field.setVisible(true);
                }
            }
        }
    }


    /**
     * small edits to initial method made by Ekkart Kindler.
     * @author s205444, Lucas
     * @return returns a random command card
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        ArrayList<Command> commandArrayList = new ArrayList<>();
        for(int i = 0; i < 8; i++){
            commandArrayList.add(commands[i]);
        }
        int random = (int) (Math.random() * commandArrayList.size());
        return new CommandCard(commandArrayList.get(random));
    }

    /**
     * Checks for a winner in the game.
     * @author s205444, Lucas
     * @param player takes the current player
     *
     */

    public void chooseWinner(Player player) {
        Alert winMsg = new Alert(Alert.AlertType.INFORMATION, "Player \"" + player.getName() + "\" won.");
        this.winnerFound = true;
        winMsg.showAndWait();
    }

    /**
     * @author Ekkart Kindler
     * Ends the programming phase and sets phase to ACTIVATION:
     */
    public void finishProgrammingPhase() {
        if(this.nullRegisters()){
            Frame frame = new Frame();
            JOptionPane.showMessageDialog(frame,"Error, none of the robots have cards in registers.");
            return;
        }
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        //board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * @author Ekkart Kindler
     * Makes programming fields visible.
     * @param register used to check that register number is less than a player's actual registers.
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * @author Ekkart Kindler
     * makes programming fields invisible
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Executes robots programs.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Executes programs beyond the first step and enables step counting.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     * Continutes execution of programming until phase is over.
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * Made edits to original version provided by Ekkart Kindler.
     * @author s205444, Lucas and Mike
     * Executes the next steps in the game.
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                rebootCard choice = currentPlayer.getProgramField(step).getCard2();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    executeCommand(currentPlayer, command);
                } else if (choice != null && currentPlayer.NeedReboot()) {
                    RebootTokens.Choose choose = choice.choose;
                    if (choose.Interactive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    }
                    PickReboot_heading(currentPlayer, choose);
                }
                for (Player player : this.board.getPlayers()) {
                    for (FieldAction fieldAction : player.getSpace().getActions()) {
                        if (winnerFound) {
                            break;
                            }
                        if(currentPlayer == board.getPlayerOrder().get(board.getPlayerOrder().size()-1)) {
                            fieldAction.doAction(this, player.getSpace());
                            player.getSpace().playerChanged();
                        }

                        }
                    }
                if(board.getPlayerOrder().indexOf(currentPlayer) == board.getPlayerOrder().size()-1) {
                    step++;
                }
                board.setCurrentPlayer();
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);

                } else {
                    startProgrammingPhase();
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * @author Ekkart Kindler
     * Executes a specific command for a robot.
     * @param option One of the command ENUMS, i.e. LEFT, RIGHT, FORWARD.
     */
    public void executeCommandOptionAndContinue(@NotNull Command option) {
        Player currentPlayer = board.getCurrentPlayer();
        if (currentPlayer != null && board.getPhase() == Phase.PLAYER_INTERACTION && option != null) {
            board.setPhase(Phase.ACTIVATION);
            executeCommand(currentPlayer, option);

            if (board.getPlayerOrder().indexOf(currentPlayer) != board.getPlayerOrder().size() - 1) {
                board.setCurrentPlayer();
            }
            else {
                board.setCurrentPlayer();
                int step = board.getStep() + 1;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);

                } else {
                    startProgrammingPhase();
                }
            }
        }

    }

    /**
     * @author Everyone, small edits by s205444, Lucas
     * Executes a command for the player dependent on the type of command.
     * @param player the current player
     * @param command the command that player is trying to execute.
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case TRIPLE_FORWARD:
                    this.tripleForward(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case BACK_UP:
                    this.backUp(player);
                    break;
                case SPAM:
                    this.spam(player);
                    break;
                case TROJAN_HORS: //Will be implemented later.
                    this.trojan(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * Recursive method that moves a player to a certain space depending on the parameters's values.
     * @author s205444 Lucas
     * @param player  the player being moved
     * @param space   the space being moved to.
     * @param heading the heading of the intially moving player.
     * @throws ImpossibleMoveException If a move is not possible, an impossibleMoveException is thrown.
     */
    public void moveToSpace(Player player, Space space, Heading heading) throws ImpossibleMoveException {
        if(space == null){
            Pit tempPit = new Pit();
            tempPit.doAction(this, player.getSpace());
            return;
        }
        if(player.NeedReboot()){
            return;
        }

        for (FieldAction fa : player.getSpace().getActions()) {
            if (fa instanceof Laser) {
                fa.doAction(this, player.getSpace());
            }
        }
        Player neighbourPlayer = space.getPlayer();
        boolean hasAnyWalls = player.getSpace().getWalls().isEmpty();

        if (!hasAnyWalls) {
            for (Heading header : player.getSpace().getWalls()) {
                if (player.getHeading() == header) {
                    space = player.getSpace();
                    throw new ImpossibleMoveException(player, space, heading);
                }
            }
        }
        Space target = space;
        boolean targetHasWalls = target.getWalls().isEmpty();
        if (!targetHasWalls) {
            for (Heading header : target.getWalls()) {
                Heading headerlist = header.next().next();
                if (headerlist == heading) {
                    throw new ImpossibleMoveException(player, space, heading);
                }
            }
        }

        if(!player.hasUpgrade(Upgrade.HOVER_UNIT)) {
            for (FieldAction fa : player.getSpace().getActions()) {
                if (fa instanceof Pit) {
                    fa.doAction(this, player.getSpace());
                    return;
                }
            }
        }

        if (target != null && neighbourPlayer != null) {
            try {
                Space neighbourSpace = board.getNeighbour(neighbourPlayer.getSpace(), heading);

                moveToSpace(neighbourPlayer, neighbourSpace, heading);
            } catch (Exception e) {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        if(target !=null){
            player.setSpace(space);
        }
        else {
            throw new ImpossibleMoveException(player, space, heading);
        }
    }

    /**
     * @author s205444, Lucas
     * Moves a player one step forward, except if the player has a BRAKE upgrade.
     * @param player the currentplayer trying to move forward.
     */

    public void moveForward(@NotNull Player player) {

        if(player.hasUpgrade(Upgrade.BRAKES)){
            ArrayList<String> choices = new ArrayList<>();
            choices.add("Yes");
            choices.add("No");
            ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
            dialog.setHeaderText("Move only 0 forward?");

            Optional<String> result = dialog.showAndWait();
            if(result.isPresent()){
                if(result.get().equals("Yes"))
                    return;
            }
        }
        Space neighbourSpace = board.getNeighbour(player.getSpace(), player.getHeading());
            try
            {
                moveToSpace(player, neighbourSpace, player.getHeading());
            } catch (ImpossibleMoveException e){
                System.out.println("Move impossible");
            }
    }


    /**
     * @author everyone
     * Moves a player two steps forward.
     * @param player the currentplayer trying to move.
     */

    public void fastForward(@NotNull Player player) {
            moveForward(player);
            moveForward(player);


    }

    /**
     * @author everyone
     * Moves a player three steps forward.
     * @param player the current player being moved.
     */

    public void tripleForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);
        moveForward(player);
    }
    /**
     * @author everyone
     * Makes the player move one space backwards
     but does not change heading
     The method should in the future be
     changed as for now there is no boundaries on the board
     @param player the current player.
     */

    public void backUp(@NotNull Player player){
        uTurn(player);
        moveForward(player);
        uTurn(player);
    }

    /** Makes the robot shift heading to the right, but stays on the same space
     * @author everyone
     * @param player  the current player.
     * */

    public void turnRight(@NotNull Player player) {
        switch (player.getHeading()) {
            case SOUTH -> player.setHeading(Heading.WEST);
            case WEST -> player.setHeading(Heading.NORTH);
            case NORTH -> player.setHeading(Heading.EAST);
            case EAST -> player.setHeading(Heading.SOUTH);
        }
    }

    /** Makes the robot shift heading to the left, but stays on the same space
     * @author everyone
     * @param player current player
     *
     * */

    public void turnLeft(@NotNull Player player) {
        switch (player.getHeading()) {
            case SOUTH -> player.setHeading(Heading.EAST);
            case EAST -> player.setHeading(Heading.NORTH);
            case NORTH -> player.setHeading(Heading.WEST);
            case WEST -> player.setHeading(Heading.SOUTH);
        }
    }
    /**
     *Rotates the players heading 180 degrees
     * @author everyone
     * @param player the current player.
     * */

    public void uTurn(@NotNull Player player){
        switch (player.getHeading()) {
            case SOUTH -> player.setHeading(Heading.NORTH);
            case EAST -> player.setHeading(Heading.WEST);
            case WEST -> player.setHeading(Heading.EAST);
            case NORTH -> player.setHeading(Heading.SOUTH);
        }

    }


    /**
     * @author Ekkart Kindler
     * Moves a card from view in the controller.
     * @param source cardfield source to move from
     * @param target cardfield target to move to
     * @return true if moved, otherwise false
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /** @author Mike
     * Made to use in the player Interaction when choosing the heading for the player on the reboot token
     * @param option the option chosen when out of reboot. Left or right.
     */
    public void Reboot_choose(@NotNull RebootTokens.Choose option){
        Player player = board.getCurrentPlayer();
        if (player != null && board.getPhase() == Phase.PLAYER_INTERACTION && option != null){
            board.setPhase(Phase.ACTIVATION);
            PickReboot_heading(player, option);


            int nextPlayerNumber = board.getPlayerNumber(player) + 1;
            if (nextPlayerNumber < board.getPlayersNumber()) {
                board.setCurrentPlayer();
            } else {
                int step = board.getStep() +1;
                if (step < Player.NO_REGISTERS) {
                    makeProgramFieldsVisible(step);
                    board.setStep(step);
                    board.setCurrentPlayer();
                } else {
                    startProgrammingPhase();
                }
            }
        }

    }


    /** @author Mike
     * This method is meant to when the player choose their heading once when they are on the reboot token
      * @param player the player on reboot.
     * @param option options, either left or right.
     */
    public void PickReboot_heading(@NotNull Player player, RebootTokens.Choose option) {
        if (player.NeedReboot &&  player != null && player.board == board && option != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (option) {
                case EAST:
                    this.East(player);
                    break;
                case WEST:
                    this.West(player);
                    break;
                case SOUTH:
                    this.South(player);
                    break;
                case NORTH:
                    this.North(player);
                default:
                    // DO NOTHING (for now)
            }
            player.setNeedReboot(false);
        }

    }

    public void North(@NotNull Player player) {
        player.setHeading(Heading.NORTH);
    }

    public void East(@NotNull Player player) {
        player.setHeading(Heading.EAST);
    }

    public void South(@NotNull Player player) {
        player.setHeading(Heading.SOUTH);
    }

    public void West(@NotNull Player player) {
        player.setHeading(Heading.WEST);
    }


    /**
     * Removes a spam card and executes a random action afterwards.
     * @author s205444, Lucas
     */
    public void spam(Player player){
        player.getDamagecards().remove(Command.SPAM);
        CommandCard card = generateRandomCommandCard();
        executeCommand(player,card.command);
    }

    /**
     * Not implemented at the moment, but can be left here for now for improvement before exam.
     * @param player the player who executes the trojan.
     */
    public void trojan(Player player){
        player.getDamagecards().remove(Command.TROJAN_HORS);
        player.getDamagecards().add(Command.SPAM);
        player.getDamagecards().add(Command.SPAM);
        CommandCard card = generateRandomCommandCard();
        executeCommand(player,card.command);
    }


    /**
     * @author s205444, Lucas
     * Allows a player to buy upgrades in the shop during programming phase.
     */
    public void shop(){
        if(!board.getPhase().equals(Phase.PROGRAMMING)){
            Frame frame = new JFrame();
            JOptionPane.showMessageDialog(frame, "You can only buy during Programming phase!");
            return;
        }
        ArrayList<String> upgradeList = new ArrayList<>();
        for(Upgrade upgrade : Upgrade.values()){
            upgradeList.add(upgrade.displayName);
        }

        ArrayList<String> playerNumber = new ArrayList<>();
        for(Player pl : board.getPlayers()){
            playerNumber.add(pl.getName());
        }

        ChoiceDialog<String> playerDialog = new ChoiceDialog<>(playerNumber.get(0),playerNumber);
        playerDialog.setTitle("Player selection");
        playerDialog.setHeaderText("Select player who is buying upgrade");
        Optional<String> playerResult = playerDialog.showAndWait();
        Player player = null;
        if(playerResult.isPresent()){
            for(Player players: board.getPlayers()) {
                if(players.getName().equals(playerResult.get())) {
                    player = players;
                }
            }
        }
        else{
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(upgradeList.get(0), upgradeList);

        dialog.setTitle("Upgrade shop");
        dialog.setHeaderText("Select a card to buy:");
        Optional<String> result = dialog.showAndWait();

        if(result.isPresent()){
            Upgrade upgradeCard = null;
            String upgradeChosen = result.get();
            for(Upgrade upgrade : Upgrade.values()){
                if(upgrade.displayName.equals(upgradeChosen)){
                    upgradeCard = upgrade;
                    break;
                }
            }
            if(upgradeCard != null && player != null) {
                Frame frame = new JFrame();
                int cost = upgradeCard.cost;
                if(player.getEnergy() < cost) {
                    JOptionPane.showMessageDialog(frame, "Not enough energy: " + player.getEnergy() + " required: "
                    + cost);
                }
                else {
                    String msg = player.addUpgrade(upgradeCard);
                    JOptionPane.showMessageDialog(frame, msg);
                }
            }
        }
    }

    /**
     * Used to check whether cards have been put in the registers.
     * @author s205444, Lucas
     * @return false if no registers are set.
     */
    public boolean nullRegisters(){
        for(Player player :board.getPlayers()){
            if(player.getProgramField(0).getCard() != null)
                return false;
        }
        return true;
    }

}

