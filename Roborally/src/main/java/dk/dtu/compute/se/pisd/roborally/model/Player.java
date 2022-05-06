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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;
    public boolean NeedReboot;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;
    private int SPAMCards = 0;

    private CommandCardField[] program;
    private CommandCardField[] cards;
    private ArrayList<Command> damagecards;
    private ArrayList<Upgrade> upgradeList;

    private int checkpoints;
    private int energy;



    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.checkpoints = 0;
        this.energy = 0;
        this.damagecards = new ArrayList<>();
        this.upgradeList = new ArrayList<>();
        this.NeedReboot = false;
        this.space = null;
        this.SPAMCards = SPAMCards;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setCheckpoints(int checkpointCounter){
        this.checkpoints = checkpointCounter;
    }

    public int getCheckpoints(){
        return checkpoints;
    }
    public void setEnergy(int energy){
        this.energy += energy;
    }
    public int getEnergy(){
        return energy;
    }


    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public Space getSpace() {
        return space;
    }

    /**
     * sets a player to a specific space
     * @param space the space the player will be set to.
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    /** @author Mike
     * Clears the i card in the register for the currentplayer
     * @param i used to figure out the program field
     */
    public void clearRegister(int i ){
        CommandCardField field = this.getProgramField(i);
        field.setCard(null);
        field.setVisible(true);
    }

    /** @author Mike
     * Clears the card the player has in their hand
     * @param i use to figure out the card
     */
    public void clearCards(int i){
        CommandCardField field = this.getCardField(i);
        field.setCard(null);
        field.setVisible(true);
    }

    /** @author Mike
     * To get the status of a robot is needing to reboot or not
     * @return boolean status of needing a reboot.
     */
    public boolean NeedReboot(){
        return NeedReboot;
    }

    /** @author Mike
     * To make a parameter for methods so that the robots dont reboot all the time but it can be turned off again
     * @param reboot sets reboot to true or false
     */
    public void setNeedReboot(boolean reboot){
        this.NeedReboot = reboot;
    }

    public void setDamagecards(Command card){
        this.damagecards.add(card);
    }
    public ArrayList<Command> getDamagecards(){
        return this.damagecards;
    }

    /**
     * adds an upgrade to a player's upgradelist
     * @author s205444, Lucas
     * @param upgrade One of the ENUM Upgrades
     * @return string dependent on whether sucessfully upgraded or not.
     */
    public String addUpgrade(Upgrade upgrade) {
        if(this.upgradeList.isEmpty()){
            upgradeList.add(upgrade);
            return "Upgrade succesfully added.";
        }
        else if (upgradeList.size() < 4){
            upgradeList.add(upgrade);
            return "Upgrade succesfully added.";
        }
        else
            return "You cannot have more than three upgrades.";
    }

    /**
     * returns true if a robot has the current upgrade, otherwise false.
     * @author s205444, Lucas
     * @param upgrade checks for one of the ENUM Upgrades
     * @return true or false
     */

    public boolean hasUpgrade(Upgrade upgrade){
        if(!upgradeList.isEmpty()) {
            for (Upgrade listUpgrade : this.upgradeList) {
                if (upgrade.equals(listUpgrade)) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Upgrade> getUpgradeList() {
        return upgradeList;
    }

    public int getSPAMCards() {
        return SPAMCards;
    }

    public void setSPAMCards(int SPAMCards) {
        this.SPAMCards = SPAMCards;
    }
}

