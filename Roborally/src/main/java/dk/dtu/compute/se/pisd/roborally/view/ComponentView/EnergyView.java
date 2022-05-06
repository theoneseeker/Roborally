package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Components.EnergyCube;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * @author s205444, Lucas
 *
 */

public class EnergyView {
    /**
     * Draws an energy cube on a specified space.
     * @author s205444, Lucas
     * @param spaceView Used to update the view of the space.
     * @param fieldA Used to check the amount of energy on an energy object space.
     */
    public static void drawEnergy(SpaceView spaceView, FieldAction fieldA){
        EnergyCube energyCube = (EnergyCube) fieldA;
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_WIDTH);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        try {
            Image checkpointImg = new Image("Components/Energy.png", 50, 50, true, true);
            gc.drawImage(checkpointImg, 0, 0);
        }
        catch (Exception e){
            System.out.print("Energy.png not found.");
            return;
        }
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(1);
        gc.strokeText(String.valueOf(energyCube.getEnergy()), SpaceView.SPACE_WIDTH*0.8, SpaceView.SPACE_WIDTH*0.5);
        spaceView.getChildren().add(canvas);
    }
}
