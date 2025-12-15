package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class LargeArea extends MazeArea {

    public LargeArea(int setKey, int setDiff){
        super(setKey, setDiff, "LargeArea");
    }

    @Override
    public void createArea(){
        registerActor(new Background(this, getAreaSize()));
        //sets up the different sprites
        super.createArea();
        super.randomKey(getKeyVal());
        createLogMonsters();
    }
    public String getTitle(){return "ICMaze/LargeArea["+getKeyVal()+"]";}
}