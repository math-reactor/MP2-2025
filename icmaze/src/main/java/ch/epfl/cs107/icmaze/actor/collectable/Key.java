package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Class, which represents the collectible Key, which is spawned in the Spawn area, in every MazeArea, and when the ICMazeBoss is beaten
 */
public class Key extends Equipment {
    private final int ID; //the Key's ID, which is set as a constant

    /**
     * Constructor for the Key class, which initializes its sprite and initializes its constant ID
     * @param area the ICMazeAre in which the Key is created in
     * @param coords the default coordinates of the Key in area
     */
    public Key(Area area, DiscreteCoordinates coords, int setID){
        super(area, coords);
        setUI(new Sprite("icmaze/key", .75f, .75f, this));
        ID = setID;
    }

    /**
     * Getter for the Key's ID
     * @return the Key's ID, an int
     */
    public int getID(){return ID;}

    /**
     * Method, which draws the Key on the provided canvas, as long as the Key hasn't been collected yet
     * @param canvas the Canvas in which the Key will be drawn in
     */
    public void draw(Canvas canvas){
        if (!isCollected()){
            super.draw(canvas);
        }
    }

    /**
     * method, allows other Interactors to interact with the Key
     * @param v the other Interactor, which wants to interact with the Key
     * @param isCellInteraction A boolean, which gives whether this is a cell interaction or not
     */
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
        collect();
    }
}
