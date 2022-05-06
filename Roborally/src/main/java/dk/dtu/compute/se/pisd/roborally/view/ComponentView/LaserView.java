package dk.dtu.compute.se.pisd.roborally.view.ComponentView;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Components.Laser;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static javafx.scene.paint.Color.RED;

/**
 * @author s205444 Lucas
 */

public class LaserView {
    /**
     * Draws a startpoint on a specific space if it has a checkpoint object.
     * @author s205444, Lucas
     * @param spaceView SpaceView object used to update the view.
     */
    public static void drawLaser(SpaceView spaceView, FieldAction fa){
        Laser laser = (Laser) fa;
        try{
            Image north = new Image("Components/LaserNORTH.png",30,30, true, true);
            Image south = new Image("Components/LaserSOUTH.png",30,30, true, true);
            Image west = new Image("Components/LaserWEST.png",30,30, true, true);
            Image east = new Image("Components/LaserEAST.png",30,30, true, true);

        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_WIDTH);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(RED);
        gc.setLineWidth(2);

        switch(laser.getHeading()){
            case SOUTH -> {
                    if(laser.getLaserStrength() == 3) {
                        gc.strokeLine(SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/2, 0);
                        gc.strokeLine(SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/3, 0);
                        gc.strokeLine(SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT*0.65, 0);
                    }
                    else if(laser.getLaserStrength() == 2){
                        gc.strokeLine(SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/3, 0);
                        gc.strokeLine(SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT*0.65, 0);
                    }
                    else
                        gc.strokeLine(SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/2, 0);

                if (!laser.getMiddle()) {
                    gc.drawImage(south,SpaceView.SPACE_WIDTH/5,0);
                }
                break;
            }
            case NORTH -> {
                if(laser.getLaserStrength() == 3) {
                    gc.strokeLine(SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/2, 0);
                    gc.strokeLine(SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/3, 0);
                    gc.strokeLine(SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT*0.65, 0);
                }
                else if(laser.getLaserStrength() == 2){
                    gc.strokeLine(SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/3, 0);
                    gc.strokeLine(SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT*0.65, 0);
                }
                else
                    gc.strokeLine(SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/2, 0);

                if (!laser.getMiddle()) {
                    gc.drawImage(north,SpaceView.SPACE_WIDTH/5,SpaceView.SPACE_HEIGHT*0.70);
                }
                break;
            }
            case WEST -> {
                if(laser.getLaserStrength() == 3) {
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/2);
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT/3);
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT*0.65);
                }
                else if(laser.getLaserStrength() == 2){
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT/3);
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT*0.65);
                }
                else
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT/2);

                if (!laser.getMiddle()) {
                    gc.drawImage(west,SpaceView.SPACE_WIDTH*0.7,SpaceView.SPACE_HEIGHT/5);
                }
                break;
            }
            case EAST -> {
                if(laser.getLaserStrength() == 3) {
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT/2);
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT/3);
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT*0.65);
                }
                else if(laser.getLaserStrength() == 2){
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/3, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT/3);
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH*0.65, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT*0.65);
                }
                else
                    gc.strokeLine(0, SpaceView.SPACE_WIDTH/2, SpaceView.SPACE_HEIGHT, SpaceView.SPACE_HEIGHT/2);

                if (!laser.getMiddle()) {
                    gc.drawImage(east,0,SpaceView.SPACE_HEIGHT/5);
                }
            }
        }
        spaceView.getChildren().add(canvas);
        }
        catch(Exception e){
            System.out.println("Image file not found");
        }
    }
}
