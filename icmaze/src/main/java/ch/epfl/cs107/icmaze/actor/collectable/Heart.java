package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Class, which represents the collectible Hearts, which are spawned with a certain probability, when a rock is broken
 */
public class Heart extends Consumable  {
    //Constants
    private final static int ANIMATION_DURATION = 24;

    /**
     * Constructor for the Heart Class, which initializes its UI elements
     * @param area the ICMazeArea in which the Heart is created
     * @param coords the DiscretePosition at which the Heart will be placed
     */
    public Heart(Area area, DiscreteCoordinates coords){
        super(area, coords, ANIMATION_DURATION);
        setUI(new Animation("icmaze/heart", 4, 1, 1, this , 16, 16, ANIMATION_DURATION/4, true));
    }

    /**
     * Method, which draws the Heart on the provided canvas, as long as the Heart hasn't been collected yet
     * @param canvas the Canvas in which the Heart will be drawn in
     */
    @Override
    public void draw(Canvas canvas) {
        if (!isCollected()) {
            super.draw(canvas);
        }
    }
    /**
     * method, allows other Interactors to interact with the Heart
     * @param v the other Interactor, which wants to interact with the Heart
     * @param isCellInteraction A boolean, which gives whether this is a cell interaction or not
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
        collect(); //collects / removes the Heart from the current ICMazeArea
    }
}
