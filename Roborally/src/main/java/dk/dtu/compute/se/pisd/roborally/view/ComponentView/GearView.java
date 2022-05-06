package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Components.Gear;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * @author s205444, Lucas
 */
public class GearView {
    /**
     * Draws a gear on the given space.
     * @param spaceView used to update the view.
     * @param fieldAction used to specify the gear's direction.
     */
    public static void drawGear(SpaceView spaceView, FieldAction fieldAction) {
        Gear gear = (Gear) fieldAction;
        Heading header = gear.getHeading();
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Image gearRight = new Image("Components/gearRight.png", 50, 50, true, true);
        Image gearLeft = new Image("Components/gearleft.png", 50, 50, true, true);
        switch (header) {
            case WEST -> {gc.setStroke(Color.GREEN); gc.drawImage(gearLeft, 0,0);}
            case EAST -> {gc.setStroke(Color.RED); gc.drawImage(gearRight,0,0);}
        }
        spaceView.getChildren().add(canvas);
    }
}