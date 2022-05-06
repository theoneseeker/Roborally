package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * @author s201010 Oline
 */

public class AntennaView {
    /**
     * Draws an Antenna on a space which will be used to determine priority (only one space per board)
     * @author s201010, Oline
     * @param spaceView SpaceView object used to update the view.
     */
    public static void drawAntenna(SpaceView spaceView){
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_WIDTH);
        GraphicsContext gc = canvas.getGraphicsContext2D();


        try{
            Image Antenna = new Image("Components/Antenna.png", 50,50, true, true);
            gc.drawImage(Antenna,0,0);
        }
        catch(Exception e){
            System.out.print("cannot find image file for Antenna");
        }
        spaceView.getChildren().add(canvas);
    }
}