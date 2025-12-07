package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;

public class Spawn extends ICMazeArea {
    public Spawn(){
        super("SmallArea");
    }
    public void createArea(){
        registerActor(new Background(this, name));
        //sets up the different sprites
        super.addItem(new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5,4)));
        super.addItem(new Heart(this, new DiscreteCoordinates(4,5)));
        super.addItem(new Key(this, Orientation.DOWN, new DiscreteCoordinates(6,5), Integer.MAX_VALUE));
    }
    public String getTitle(){return "ICMaze/Spawn";}
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
