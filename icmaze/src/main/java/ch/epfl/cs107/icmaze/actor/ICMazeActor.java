package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.handler.Damageable;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.Collections;
import java.util.List;

public abstract class ICMazeActor extends MovableAreaEntity implements Interactable, Damageable {
    private boolean recovering = false;
    protected Health health;
    protected int drawFrame;
    private Cooldown cd;

    public ICMazeActor(Area setArea, Orientation setOrient, DiscreteCoordinates setCoords){
        super(setArea, setOrient, setCoords);
    }

    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setCurrentPosition(position.toVector());
        resetMotion();
        setOwnerArea(area);
    }
    public boolean takeCellSpace(){
        return false;
    }
    public boolean isCellInteractable(){return true;}
    public boolean isViewInteractable(){return false;}
    public void acceptInteraction(AreaInteractionVisitor vis, boolean isCellInteraction){}
    protected DiscreteCoordinates getNextPosition(Orientation orient, DiscreteCoordinates pos){
        DiscreteCoordinates newPos = getCurrentMainCellCoordinates();
        switch (orient){
            case UP -> newPos = new DiscreteCoordinates(pos.x, pos.y+1);
            case DOWN -> newPos = new DiscreteCoordinates(pos.x, pos.y-1);
            case RIGHT -> newPos = new DiscreteCoordinates(pos.x+1, pos.y);
            case LEFT -> newPos = new DiscreteCoordinates(pos.x-1, pos.y);
        }
        return newPos;
    }
    @Override
    public List<DiscreteCoordinates > getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
    public void damageActor(int damage){
        if (!recovering){
            health.decrease(damage);
            drawFrame = 0;
            recovering = true;
        }
    }
    protected void setRecovery(boolean state){
        recovering = state;
    }
    protected boolean getRecovery(){return recovering;}
    protected void setCd(){
        cd = new Cooldown(health.STANDARD_COOLDOWN_TIME);
    }
    protected void handleRecovery(float deltaTime){
        if (recovering){
            recovering = !cd.ready(deltaTime);
        }
        else {
            cd.reset();
        }
    }
    protected void handleAnim(OrientedAnimation UI, Canvas canvas){
        if (recovering){ //oscillating frames when damaged
            if (drawFrame % 3 == 0){
                UI.draw(canvas);
            }
            drawFrame += 1;
            health.draw(canvas);
        }
        else {
            UI.draw(canvas);
        }
    }
}
