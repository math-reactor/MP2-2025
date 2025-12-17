package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Abstract class, represents the concept of collectible actors in general
 */
public abstract class Collectibles extends CollectableAreaEntity  {
    /**
     * Constructor for the Collectible class. Calls its super-constructor
     * @param area the ICMazeArea in which the Collectible will be created
     * @param orient the default Orientation of the Collectible
     * @param coords the default coordinates of the Collectible in this area
     */
    public Collectibles(Area area, Orientation orient, DiscreteCoordinates coords){
        //constructor for equipment
        super(area, orient, coords);
    }

    /**
     * Constructor for the Collectible class. Calls its super-constructor, and has its Orientation set as DOWN by default
     * @param area the ICMazeArea in which the Collectible will be created
     * @param coords the default coordinates of the Collectible in this area
     */
    public Collectibles(Area area, DiscreteCoordinates coords){
        //Constructor for non-equipment
        super(area, Orientation.DOWN, coords);
    }

    /**
     * Abstract method to be redefined, which draws the Collectible on the provided canvas
     * @param canvas the Canvas in which the Collectible will be drawn in
     */
    public abstract void draw(Canvas canvas);

    /**
     * Method, which returns the occupied DiscreteCoordinated by the Collectible
     * @return List<DiscreteCoordinates>, the list containing the DiscreteCoordinates occupied by the Collectible
     */
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * Method, which returns whether the Collectible blocks the DiscreteCoordinates it occupies
     * @return false, as it must be walked upon, to be collected
     */
    public boolean takeCellSpace(){return false;}

    /**
     * Method, which returns whether the Collectible can be interacted with through cell interactions
     * @return true, as it must be walked upon, to be collected -> cell interaction
     */
    public boolean isCellInteractable(){return true;}

    /**
     * Method, which returns whether the Collectible can be interacted with through view interactions
     * @return false, as it must be walked upon, to be collected -> view interactions not allowed
     */
    public boolean isViewInteractable(){return false;}

    /**
     * Method, which updates the Collectible's UI
     * @param deltatime the time interval
     */
    public void update(float deltatime){
        super.update(deltatime);
    }
}
