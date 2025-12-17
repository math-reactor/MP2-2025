package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Class, which represents the collectible Pickaxe, which is spawned in the Spawn area
 */
public class Pickaxe extends Equipment  {
    /**
     * Constructor for the Pickaxe class, which initializes its sprite
     * @param area the ICMazeAre in which the Pickaxe is created in
     * @param coords the default coordinates of the Pickaxe in area
     */
    public Pickaxe(Area area, DiscreteCoordinates coords){
        super(area, coords);
        setUI(new Sprite("icmaze/pickaxe", .75f, .75f, this));
    }

    /**
     * Method, which draws the Pickaxe on the provided canvas, as long as the Pickaxe hasn't been collected yet
     * @param canvas the Canvas in which the Pickaxe will be drawn in
     */
    @Override
    public void draw(Canvas canvas){
        if (!isCollected()){
            super.draw(canvas);
        }
    }

    /**
     * method, allows other Interactors to interact with the Pickaxe
     * @param v the other Interactor, which wants to interact with the Pickaxe
     * @param isCellInteraction A boolean, which gives whether this is a cell interaction or not
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
        collect();  //collects / removes the Pickaxe from the current ICMazeArea
    }
}
