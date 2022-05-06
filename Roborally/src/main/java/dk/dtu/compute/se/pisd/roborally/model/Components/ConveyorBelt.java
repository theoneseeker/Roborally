package dk.dtu.compute.se.pisd.roborally.model.Components;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * blueprint for conveyorbelt component on baord. A robot landing on here will move in a given heading dependent
 * on the velocity.
 * @author s205444, Lucas
 */

public class ConveyorBelt extends FieldAction {

    private int velocity;

    Heading heading;


    public Heading getHeading() {
        return heading;
    }

    /**
     * Moves a player in the conveyorbelt heading either one step or twice dependent on the velocity.
     * @author s205444, Lucas
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true or false depending on whether action was performed.
     */

    @Override
    public boolean doAction(GameController gameController, Space space){

        for(int i = 0; i < velocity; i++) {
            try {
                Space neighbourOfConveyorHeading = gameController.board.getNeighbour(space.getPlayer().getSpace(), this.heading);

                gameController.moveToSpace(space.getPlayer(), neighbourOfConveyorHeading, this.heading);
            }
            catch(Exception e){
                return false;
            }
        }
        return false;
    }
}
