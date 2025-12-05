package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;


public class BossArea extends ICMazeArea {
    public BossArea(){
        super("SmallArea");
    }
    public void createArea(){
        registerActor(new Background(this, name));
        //Portal westPortal = getPortal(AreaPortals.W);

        //westPortal.setDestinationArea("SpawnArea");

        //westPortal.setDestinationCoordinates(
                //new DiscreteCoordinates(8, 4)
        //);

        //westPortal.setState(PortalState.OPEN);

    }
    public String getTitle(){return "ICMaze/Boss";}
}
