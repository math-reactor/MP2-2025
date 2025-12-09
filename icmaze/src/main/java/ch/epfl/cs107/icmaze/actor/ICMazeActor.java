package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public abstract class ICMazeActor extends MovableAreaEntity implements Interactable {
    private String parentArea;
    public ICMazeActor(Area setArea, Orientation setOrient, DiscreteCoordinates setCoords){
        super(setArea, setOrient, setCoords);
        parentArea = setArea.getTitle();
    }
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setCurrentPosition(position.toVector());
        resetMotion();
        setOwnerArea(area);
    }
    public void exitArea(){
        getOwnerArea().unregisterActor(this);
    }
    public boolean takeCellSpace(){
        return false;
    }
    public boolean isCellInteractable(){return true;}
    public boolean isViewInteractable(){return false;}
    public void acceptInteraction(AreaInteractionVisitor vis, boolean isCellInteraction){}
    @Override
    public List<DiscreteCoordinates > getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}
