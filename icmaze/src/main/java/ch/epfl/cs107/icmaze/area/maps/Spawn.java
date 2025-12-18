package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Class, which allows for the creation of safe, initial spawn locations for the player
*/
public class Spawn extends ICMazeArea {
    private boolean treasureSpawned; //shows when the treasure has been spawned

    /**
     * Constructor for the Spawn area
     * @param keyVal the key value that is associated with this area (the one that is needed to enter the area)
     */
    public Spawn(int keyVal){
        super("SmallArea", keyVal);
    }

    /**
     * method, which creates this Spawn area, by creating the background and then adding all necessary actors in
     */
    @Override
    public void createArea(){
        registerActor(new Background(this, getAreaSize()));
        //sets up the different collectibles in the spawn area
        super.addItem(new Pickaxe(this, new DiscreteCoordinates(5,4)));
        super.addItem(new Heart(this, new DiscreteCoordinates(4,5)));
        super.addItem(new Key(this, new DiscreteCoordinates(6,5), getKeyVal()));
    }

    /**
     * method, which will run whenever the game is beaten : Spawning of three hearts in the SpawnArea
     */
    public void onVictory(){
        if (!treasureSpawned){
            treasureSpawned = true;
            super.addItem(new Heart(this, new DiscreteCoordinates(5,5)));
            super.addItem(new Heart(this, new DiscreteCoordinates(5,4)));
            super.addItem(new Heart(this, new DiscreteCoordinates(6,5)));
        }
    }

    /**
     * method, which will returns the area's title
     */
    public String getTitle(){return "ICMaze/Spawn";}

    /**
     * method, which will redraw the current area
     */
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
