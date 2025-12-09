package ch.epfl.cs107.icmaze.actor.Boss;

import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class ICMazeBoss extends ICMazeActor {
    public ICMazeBoss(Area setArea, Orientation orient, DiscreteCoordinates setPos){
        super(setArea, orient, setPos);
    }
}
