/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Components.*;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.view.ComponentView.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 50; // 60; // 75; //made is match my screen
    final public static int SPACE_WIDTH = 50;  // 60; // 75; //made is match my screen

    public final Space space;

    public SpaceView(@NotNull Space space) {
        this.space = space;

        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        space.attach(this);

        update(space);
    }

    /**
     * updates the view of each player on each space.
     */
    private void updatePlayer() {

        Player player = space.getPlayer();
        if (player != null) {
            Polygon arrow = new Polygon(0.0, 0.0,
                    10.0, 20.0,
                    20.0, 0.0 );
            try {
                arrow.setFill(Color.valueOf(player.getColor()));
            } catch (Exception e) {
                arrow.setFill(Color.MEDIUMPURPLE);
            }

            arrow.setRotate((90*player.getHeading().ordinal())%360);
            Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_WIDTH);
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setStroke(Color.GREEN);
            gc.setLineWidth(1);
            gc.strokeText(String.valueOf(player.getCheckpoints()), SpaceView.SPACE_WIDTH*0.8, SpaceView.SPACE_WIDTH*0.8);

            this.getChildren().add(canvas);
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(1);
            gc.strokeText(String.valueOf(player.getEnergy()), SpaceView.SPACE_WIDTH*0.2, SpaceView.SPACE_WIDTH*0.8);
            this.getChildren().add(arrow);
        }
    }

    /**
     * updates view for each space on the board.
     * @author s205444, Lucas
     * @param subject The subject of the observer design pattern.
     */
    @Override
    public void updateView(Subject subject) {
        this.getChildren().clear();
        if (subject == this.space) {
            updateNormalSpace();
            if(this.space.getStartPoint()){
                StartpointView.drawStartpoint(this);
            }
            if(this.space.getAntenna()){
                AntennaView.drawAntenna(this);
            }
            if(!this.space.getWalls().isEmpty()){
                WallView.drawWall(this, this.space);
            }
            for(FieldAction fa : space.getActions()) {
                if (fa instanceof PushPanel) {
                    PushPanelView.drawPushPanel(this, fa);
                }
                else if (fa instanceof Gear) {
                    GearView.drawGear(this, fa);
                }
                else if (fa instanceof Checkpoint) {
                    CheckpointView.drawCheckpoint(this, fa);
                }

                else if (fa instanceof Pit) {
                    PitView.drawPit(this);
                }
                else if (fa instanceof ConveyorBelt) {
                    ConveyorBeltView.drawConveyorBeltView(this, fa);
                }
                else if (fa instanceof EnergyCube){
                    EnergyView.drawEnergy(this, fa);
                }
                else if(fa instanceof RebootTokens){
                    RebootTokensView.drawRebootTokens(this);
                }
                else if(fa instanceof Laser){
                    LaserView.drawLaser(this, fa);
                }
            }

            updatePlayer();
        }
    }

    /**
     * Updates each space on the baord with a grey background.
     * @author s205444 Lucas
     */
    public void updateNormalSpace(){
        Image image = new Image("Components/Space.png", 50, 50, true, true);
        Canvas canvas = new Canvas(SpaceView.SPACE_WIDTH, SpaceView.SPACE_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(image, 0,0);
        this.getChildren().add(canvas);
    }


}
