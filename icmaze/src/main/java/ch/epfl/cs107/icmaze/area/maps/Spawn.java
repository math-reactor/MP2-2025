package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;

public class Spawn extends ICMazeArea {
    public Spawn(){
        super("SmallArea");
    }
    public void createArea(){
        registerActor(new Background(this, name));
        //sets up the different sprites
        addItem(new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5,4)));
        addItem(new Heart(this, new DiscreteCoordinates(4,5), 24));
        addItem(new Key(this, Orientation.DOWN, new DiscreteCoordinates(6,5), Integer.MAX_VALUE));
        addItem(new Key(this, Orientation.DOWN, new DiscreteCoordinates(1,2), Integer.MAX_VALUE-1));
        addItem(new Rock(this, new DiscreteCoordinates(3,2)));
    }
    public String getTitle(){return "ICMaze/Spawn";}
}
