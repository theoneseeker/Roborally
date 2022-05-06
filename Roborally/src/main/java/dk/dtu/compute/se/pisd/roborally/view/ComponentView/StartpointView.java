package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * @author s205444 Lucas
 */

public class StartpointView {
    /**
     * Draws a startpoint on a specific space if it has a checkpoint object.
     * @author s205444, Lucas
     * @param spaceView SpaceView object used to update the view.
     */
    public static void drawStartpoint(SpaceView spaceView){
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_WIDTH);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        try{
            Image startpoint = new Image("Components/Start.png", 50,50, true, true);
            gc.drawImage(startpoint,0,0);
        }
        catch(Exception e){
            System.out.print("cannot find image file for startpoint");
        }
        spaceView.getChildren().add(canvas);
    }
}
