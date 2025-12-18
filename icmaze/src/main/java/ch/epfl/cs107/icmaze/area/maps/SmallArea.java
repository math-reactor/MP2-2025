package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.play.engine.actor.Background;


/**
 * Class, which represent the smallest possible maze area size
 */
public class SmallArea extends MazeArea {

    /**
     * Constructor for the SmallArea class, which initializes its attributes in ICMazeArea
     * @param setKey the ID of this area's required key
     * @param setDiff the difficulty of this area's maze
     */
    public SmallArea(int setKey, int setDiff){
        super(setKey, setDiff, "SmallArea");
    }

    /**
     * method, which does a graphical initialization of this area
     */
    @Override
    public void createArea(){
        registerActor(new Background(this, getAreaSize()));  //creation of the background based on the area's size
        super.createArea();
        super.randomKey(getKeyVal()); //creates a random key in this area with the ID of the next area
        createLogMonsters(); //creates this area's logmonsters
    }

    /**
     * method, which returns the unique ID of this SmallArea
     * @return String - the unique ID of this LargeArea in the following format - ICMaze/SmallArea[key value]
     */
    @Override
    public String getTitle(){return "ICMaze/SmallArea["+getKeyVal()+"]";}
}
