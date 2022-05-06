package dk.dtu.compute.se.pisd.roborally.model.Components;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * @author s205444, Lucas
 *
 */
public class Checkpoint extends FieldAction {
    private int checkpoints;
    public int getCheckpoints() {
        return checkpoints;
    }
    /**
     * Increases a player's checkpoint counter if they are collected in ascending order.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true or false
     */

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Player player = space.getPlayer();
        if (player != null) {
            if (player.getCheckpoints() == this.checkpoints - 1) {
                space.getPlayer().setCheckpoints(space.getPlayer().getCheckpoints() + 1);
            }

            if(player.getCheckpoints() == gameController.board.getCheckpointCounter()) {
                gameController.chooseWinner(player);
            }
            return true;
        }
        return false;
    }
}


