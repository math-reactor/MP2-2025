package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Abstract class, which represents the collectible equipment, which can be used by the player and do not have an Animation
 */
public abstract class Equipment extends Collectibles{
    private Sprite UI;

    /**
     * Constructor for the Equipment class. Calls its super-constructor with the default Orientation set as DOWN
     * @param area the ICMazeArea in which the equipment will be created in
     * @param coords the default coordinates of the Equipment in this area
     */
    public Equipment(Area area, DiscreteCoordinates coords){
        super(area, Orientation.DOWN, coords);
    }

    /**
     * Setter for the equipment's sprite
     * @param setSprite the sprite that will be set as the equipment's
     */
    public void setUI(Sprite setSprite){
        UI = setSprite;
    }

    /**
     * method to draw the Equipment on the provided canvas
     * @param canvas the Canvas on which the Equipment will be drawn on
     */
    @Override
    public void draw(Canvas canvas){
        UI.draw(canvas);
    }
}
