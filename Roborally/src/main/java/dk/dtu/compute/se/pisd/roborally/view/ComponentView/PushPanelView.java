package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Components.PushPanel;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * @author s205444, Lucas
 */

public class PushPanelView {
    /**
     * Blueprint for printing a view of a pushpanel.
     * @author s205444, Lucas
     * @param fa takes the FieldAction object to determine heading.
     * @param spaceView takes a SpaceView object to update view for the corresponding space.
     */
    public static void drawPushPanel(SpaceView spaceView, FieldAction fa) {
        try {
            PushPanel pushPanel = (PushPanel) fa;
            Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            javafx.scene.image.Image right = new javafx.scene.image.Image("Components/PushWEST.png", 50, 50, true, true);
            javafx.scene.image.Image left = new javafx.scene.image.Image("Components/PushEAST.png", 50, 50, true, true);
            javafx.scene.image.Image Up = new javafx.scene.image.Image("Components/PushSOUTH.png", 50, 50, true, true);
            javafx.scene.image.Image Down = new Image("Components/PushNORTH.png", 50, 50, true, true);

            switch (pushPanel.getHeading()) {
                case SOUTH -> gc.drawImage(Down, 0, 0);
                case NORTH -> gc.drawImage(Up, 0, 0);
                case WEST -> gc.drawImage(left, 0, 0);
                case EAST -> gc.drawImage(right, 0, 0);
            }
            spaceView.getChildren().add(canvas);
        }
        catch(Exception e){
            System.out.print("Image of Pushpanels could not be found");
        }
    }
}
