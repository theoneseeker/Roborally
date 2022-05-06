package dk.dtu.compute.se.pisd.roborally.model.Components;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * @author s205444, Lucas
 */
public class PushPanel extends FieldAction {
    private Heading heading;


    public Heading getHeading(){
        return heading;
    }

    /**
     * Pushes a robot in the direction of the Push panel.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return true or false
     */

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Heading pushHeading = this.heading;
        Space neighourSpace = gameController.board.getNeighbour(space, pushHeading);
        try {
            gameController.moveToSpace(space.getPlayer(), neighourSpace, pushHeading);
            return true;
        }
        catch(Exception e){
            System.err.println("error moving from push pannel");
        }

        return false;
    }
}
