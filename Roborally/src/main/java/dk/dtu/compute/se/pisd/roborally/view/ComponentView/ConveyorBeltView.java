package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Components.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * @author s205444, Lucas
 */
public class ConveyorBeltView {
    /**
     * @author s20544, Lucas
     * @param spaceView used to update view of the game.
     * @param fieldAction Used to check the heading of the present conveyorbelt.
     */
    public static void drawConveyorBeltView(SpaceView spaceView, FieldAction fieldAction) {
        ConveyorBelt conveyorBelt = (ConveyorBelt) fieldAction;
        Heading heading = conveyorBelt.getHeading();
        spaceView.getChildren().clear();
        try {
            Image right = new Image("Components/Right.png", 50, 50, true, true);
            Image left = new Image("Components/Left.png", 50, 50, true, true);
            Image Up = new Image("Components/Up.png", 50, 50, true, true);
            Image Down = new Image("Components/Down.png", 50, 50, true, true);

            Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            switch (heading) {
                case WEST -> gc.drawImage(left, 0, 0);
                case EAST -> gc.drawImage(right, 0, 0);
                case NORTH -> gc.drawImage(Up, 0, 0);
                case SOUTH -> gc.drawImage(Down, 0, 0);
            }


            spaceView.getChildren().add(canvas);
        }
            catch(Exception e){
            System.out.print("Could not find image files for ConveyorBelts");
        }
    }
}
