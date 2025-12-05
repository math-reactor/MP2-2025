package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.CollectableAreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public abstract class Collectibles extends CollectableAreaEntity{
    public Collectibles(Area area, Orientation orient, DiscreteCoordinates coords){
        //constructor for equipment
        super(area, orient, coords);
    }
    public Collectibles(Area area, DiscreteCoordinates coords){
        //Constructor for non-equipment
        super(area, Orientation.DOWN, coords);
    }
    public abstract void draw(Canvas canvas);
    public List<DiscreteCoordinates > getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    public boolean takeCellSpace(){return false;}
    public boolean isCellInteractable(){return true;}
    public boolean isViewInteractable(){return true;}
    public void update(float deltatime){
        super.update(deltatime);
    }
}
