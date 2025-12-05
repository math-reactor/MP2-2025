package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.play.engine.actor.Background;

public class LargeArea extends MazeArea {
    public LargeArea(int setKey, int setDiff){
        super(setKey, setDiff, "LargeArea");
    }
    public void createArea(){
        registerActor(new Background(this, name));
        //sets up the different sprites
        super.createArea();
        super.randomKey(Integer.MAX_VALUE);
    }
    public String getTitle(){return "ICMaze/LargeArea["+exitKey+"]";}
}