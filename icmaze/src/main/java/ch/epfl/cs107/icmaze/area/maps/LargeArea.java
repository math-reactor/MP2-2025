package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.play.engine.actor.Background;


/**
 * Class, which represent the largest possible maze area size
 */
public class LargeArea extends MazeArea {

    /**
     * Constructor for the LargeArea class, which initializes its attributes in ICMazeArea
     * @param setKey the ID of this area's required key
     * @param setDiff the difficulty of this area's maze
     */
    public LargeArea(int setKey, int setDiff){
        super(setKey, setDiff, "LargeArea");
    }

    @Override
    /**
     * method, which does a graphical initialization of this area
     */
    public void createArea(){
        registerActor(new Background(this, getAreaSize())); //creation of the background based on the area's size
        super.createArea();
        super.randomKey(getKeyVal()); //creates a random key in this area with the ID of the next area
        createLogMonsters(); //creates this area's logmonsters
    }

    @Override
    /**
     * method, which returns the unique ID of this LargeArea
     * @return String - the unique ID of this LargeArea in the following format - ICMaze/LargeArea[key value]
     */
    public String getTitle(){return "ICMaze/LargeArea["+getKeyVal()+"]";}
}