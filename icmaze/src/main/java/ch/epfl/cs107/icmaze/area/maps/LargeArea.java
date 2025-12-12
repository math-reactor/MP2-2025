package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.play.engine.actor.Background;

public class LargeArea extends MazeArea {
    public LargeArea(int setKey, int setDiff){
        super(setKey, setDiff, "LargeArea");
    }
    public void createArea(){
        registerActor(new Background(this, getAreaSize()));
        //sets up the different sprites
        super.createArea();
        super.randomKey(getKeyVal());
    }
    public String getTitle(){return "ICMaze/LargeArea["+getKeyVal()+"]";}
}