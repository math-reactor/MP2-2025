package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

public class Key extends Equipment {
    private final int ID;
    public Key(Area area, Orientation orient, DiscreteCoordinates coords, int setID){
        super(area, orient, coords);
        setUI(new Sprite("icmaze/key", .75f, .75f, this));
        ID = setID;
    }
    public int getID(){return ID;}
    public void draw(Canvas canvas){
        if (!isCollected())
        super.draw(canvas);
    }
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        if (isCellInteraction){
            ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
            collect();
        }
    }
}
