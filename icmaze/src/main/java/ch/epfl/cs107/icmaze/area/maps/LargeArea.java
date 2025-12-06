package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.play.engine.actor.Background;

import java.net.Inet4Address;

public class LargeArea extends MazeArea {
    public LargeArea(int setKey, int setDiff){
        super(setKey, setDiff, "LargeArea");
    }
    public void createArea(){
        registerActor(new Background(this, name));
        //sets up the different sprites
        super.createArea();
        super.randomKey(exitKey-1);
        Portal westMAPortal = getPortal(AreaPortals.W);
        westMAPortal.setState(PortalState.OPEN);
        westMAPortal.setDestinationCoordinates(AreaPortals.W, "MediumArea");
        Portal eastMAPortal = getPortal(AreaPortals.E);
        eastMAPortal.setState(PortalState.LOCKED);
        eastMAPortal.setDestinationCoordinates(AreaPortals.E, "SmallArea");

        westMAPortal.setDestinationArea("ICMaze/MediumArea["+ (exitKey+1)+"]");
        eastMAPortal.setDestinationArea("ICMaze/Boss");
    }
    public String getTitle(){return "ICMaze/LargeArea["+exitKey+"]";}
}