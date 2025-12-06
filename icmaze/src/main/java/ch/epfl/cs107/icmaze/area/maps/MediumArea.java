package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.play.engine.actor.Background;

public class MediumArea extends MazeArea {
    public MediumArea(int setKey, int setDiff){
        super(setKey, setDiff, "MediumArea");
    }
    public void createArea(){
        registerActor(new Background(this, name));
        //sets up the different sprites
        super.createArea();
        super.randomKey(exitKey-1);
        Portal westMAPortal = getPortal(AreaPortals.W);
        westMAPortal.setState(PortalState.OPEN);
        westMAPortal.setDestinationCoordinates(AreaPortals.W, "SmallArea");
        Portal eastMAPortal = getPortal(AreaPortals.E);
        eastMAPortal.setState(PortalState.LOCKED);
        eastMAPortal.setDestinationCoordinates(AreaPortals.E, "LargeArea");

        westMAPortal.setDestinationArea("ICMaze/SmallArea["+(exitKey+1)+"]");
        eastMAPortal.setDestinationArea("ICMaze/LargeArea["+(exitKey-1)+"]");
    }
    public String getTitle(){return "ICMaze/MediumArea["+exitKey+"]";}
}