package dk.dtu.compute.se.pisd.roborally.model.Components;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

public class Pit extends FieldAction {

    @Override
    //En pit skal give 2 spam dmg og man skal tømme registeret af resterende kort,
    //samt man sendes til en reboot-token på samme board man er på.
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();

        //Clears the register for the player landing on the Pit, to ensure that they dont more from it.
        clearRegister(player, space);
        player.setNeedReboot(true);
        Space rebootSpace;
        Board board = gameController.board;
        for(int i = 0; i < board.width; i++) {
            for(int k = 0; k < board.height; k++){
                rebootSpace = board.getSpace(i,k);
                for(FieldAction fa: rebootSpace.getActions()){
                    if(fa instanceof RebootTokens){
                        player.setSpace(rebootSpace);
                        if(!player.hasUpgrade(Upgrade.FIREWALL)) {
                            player.getDamagecards().add(Command.SPAM);
                            player.getDamagecards().add(Command.SPAM);
                        }
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /** @author Mike
     * Clears the register for the currentplayer on the pit from the next card to execute
     * when they land on the Pit
     * @param player the player on reboot token
     * @param space the space the player is on
     */
    public void clearRegister(Player player, Space space){
        int step = space.board.getStep();
        for (int i = step + 1; i < Player.NO_REGISTERS; i++)
            player.clearRegister(i);
    }

}


