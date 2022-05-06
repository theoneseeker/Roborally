package dk.dtu.compute.se.pisd.roborally.model.Components;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RebootTokens extends FieldAction {
    /**@author Mike
     * This field action is made so that it can only happen when the player is in need of reboot
     */
    @Override
    public boolean doAction(GameController gameController, Space space) {

        Player player = space.getPlayer();

        if (player.NeedReboot()){
            //makes sure that there are no cards left in the register for the player.
            for(int i = 0; i < Player.NO_REGISTERS; i++){
                CommandCardField field = player.getProgramField(i);
                if(field.getCard2()==null)
                    player.clearCards(i);
            }
            clearPlayerRegister(player);
            CommandCardField field = player.getProgramField(4);
            field.setCard2(new rebootCard(Choose.CHOOSE_HEADING));
            field.setVisible(true);
            player.setNeedReboot(false);

        }
        return false;
    }

    /** @author Mike
     * This method should remove all the cards in the players hand and register so no action with them should be able
     * @param player the current player
     */
    public void clearPlayerRegister(Player player){
        for (int i = 0; i < Player.NO_REGISTERS; i++)
            player.clearRegister(i);
        for (int j = 0; j < Player.NO_CARDS; j++)
            player.clearCards(j);
    }


    /** @author Mike
     * Enum that gives the different headings for a player when they need to reboot
     */
    public enum Choose{
        NORTH("North"),
        SOUTH("South"),
        EAST("East"),
        WEST("West"),
        CHOOSE_HEADING("North, South, East or West", Choose.NORTH, Choose.SOUTH, Choose.EAST, Choose.WEST);

        final public String displayName;

        final private List<Choose> options;

        Choose(String displayName, Choose... choose) {
            this.displayName = displayName;
            this.options = Collections.unmodifiableList(Arrays.asList(choose));
        }

        public boolean Interactive() {
            return !options.isEmpty();
        }

        public List<Choose> getOptions() {
            return options;
        }
}

}