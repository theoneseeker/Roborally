package dk.dtu.compute.se.pisd.roborally.model.Components;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**
 * @author s205444, Lucas
 * Blueprint for Gear component in RoboRally.
 */
public class Gear extends FieldAction {
    private Heading heading;
    
    public Heading getHeading(){
        return heading;
    }

    /**
     * Turns a player left or right dependent on the gear rotation.
     * @param gameController the gameController of the respective game
     * @param space the space this action should be executed for
     * @return returns true or false dependent on whether action was executed.
     */

    @Override
    public boolean doAction(GameController gameController, Space space) {
        Heading pHeading = space.getPlayer().getHeading();
        Heading gearheading = this.heading;
        switch(gearheading){
            case EAST -> {
                space.getPlayer().setHeading(pHeading.next());
                return true;
            }
            case WEST -> {
                space.getPlayer().setHeading(pHeading.prev());
                return true;
            }
        }
        return false;
    }
}