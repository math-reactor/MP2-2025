package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;

public class SmallArea extends MazeArea {
    public SmallArea(int setKey, int setDiff){
        super(setKey, setDiff, "SmallArea");
    }
    public void createArea(){
        registerActor(new Background(this, name));
        //sets up the different sprites
        super.createArea();
        super.randomKey(Integer.MAX_VALUE-1);
        Portal westSAPortal = getPortal(AreaPortals.W);
        westSAPortal.setState(PortalState.OPEN);
        westSAPortal.setDestinationCoordinates(AreaPortals.W, "SmallArea");
        Portal eastSAPortal = getPortal(AreaPortals.E);
        eastSAPortal.setState(PortalState.LOCKED);
        eastSAPortal.setDestinationCoordinates(AreaPortals.E, "MediumArea");

        westSAPortal.setDestinationArea("ICMaze/Spawn");
        eastSAPortal.setDestinationArea("ICMaze/MediumArea["+(Integer.MAX_VALUE-1)+"]");
    }
    public String getTitle(){return "ICMaze/SmallArea["+exitKey+"]";}
}
