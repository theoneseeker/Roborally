package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

/**
 * @author s205444, Lucas
 */

public class PitView {
    /**
     * Used to update the view of a Pit.
     * @param spaceView Used to update the view.
     */
    public static void drawPit(SpaceView spaceView){
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(25);
        gc.setLineCap(StrokeLineCap.SQUARE);
        gc.strokeOval(2,2,48,48);
        spaceView.getChildren().add(canvas);
        Image pitImage = new Image("Components/Pit.png", 50, 50, true, true);
        gc.drawImage(pitImage, 0, 0);
    }
}
