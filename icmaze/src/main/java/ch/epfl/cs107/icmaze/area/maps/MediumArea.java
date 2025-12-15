package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.play.engine.actor.Background;

public class MediumArea extends MazeArea {
    public MediumArea(int setKey, int setDiff){
        super(setKey, setDiff, "MediumArea");
    }
    public void createArea(){
        registerActor(new Background(this, getAreaSize()));
        //sets up the different sprites
        super.createArea();
        super.randomKey(getKeyVal());
        createLogMonsters();
    }
    public String getTitle(){return "ICMaze/MediumArea["+getKeyVal()+"]";}
}