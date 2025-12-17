package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Abstract class, which represents the collectible consumables, which can be used by the player and have an OrientedAnimation
 */
public abstract class Consumable extends Collectibles{
    private final int animDur; //Animation duration
    private Animation UIelement;

    /**
     * Constructor for the Consumable Class, which calls its superclass and initializes the animation duration
     * @param area the ICMazeArea in which the Consumable is created
     * @param coords the DiscretePosition at which the Heart will be placed
     * @param newAnimDur the default animation duration (int)
     */
    public Consumable(Area area, DiscreteCoordinates coords, int newAnimDur){
        super(area, coords);
        animDur = newAnimDur;
    }

    /**
     * Setter for the equipment's sprite
     * @param newUI the Animation that will be set as the Consumable's
     */
    public void setUI(Animation newUI) {
        UIelement = newUI;
    }

    /**
     * method to draw the Consumable and to update its animation on the provided canvas
     * @param canvas the Canvas on which the Consumable will be drawn on
     */
    @Override
    public void draw(Canvas canvas) {
        UIelement.update(animDur);
        UIelement.draw(canvas);
    }
}
