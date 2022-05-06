package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

/**
 * @author s205444, Lucas
 *
 */

public class WallView {
    /**
     * Updates the view of a wall on the board.
     * @author s20544, Lucas
     * @param spaceView used to update the view.
     * @param space Used to check the position of the wall as specified in the .JSON file.
     */
    public static void drawWall(SpaceView spaceView, Space space) {
        try {
            List<Heading> walls = space.getWalls();
            Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            Image wallNorthSouth = new Image("Components/WallNORTHSOUTH.png", 50, 50, true, true);
            Image wallWestEast = new Image("Components/WallWESTEAST.png", 50, 50, true, true);

            for (Heading header : walls) {
                switch (header) {
                    case SOUTH -> gc.drawImage(wallNorthSouth, 0, 44);
                    case NORTH -> gc.drawImage(wallNorthSouth, 0, 0);
                    case WEST -> gc.drawImage(wallWestEast, 0, 0);
                    case EAST -> gc.drawImage(wallWestEast, 44, 0);
                }
            }
            spaceView.getChildren().add(canvas);
        }
        catch(Exception e){
            System.out.println("Image file for walls could not be found");
        }

    }
}
