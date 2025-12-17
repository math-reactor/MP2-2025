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

import java.util.Collections;
import java.util.List;

/**
 * Class, which represents the concept of actors, which evolve on a given ICMazeArea and that are capable of movement
 */
public abstract class ICMazeActor extends MovableAreaEntity implements Interactable, Damageable {
    private boolean recovering = false; //whether the actor is in its immunity phase
    protected Health health;
    protected int drawFrame; //a frame counter, to create the oscillating frame effect, during immunity
    private Cooldown cd;

    /**
     * Constructor for the ICMazeActor class, which calls its super-class to initialize this object
     * @param setArea the area where the ICMazeActor will be created in
     * @param setOrient the default orientation of the ICMazeActor, during his creation
     * @param setCoords the coordinates at which the ICMazeActor will be created
     */
    public ICMazeActor(Area setArea, Orientation setOrient, DiscreteCoordinates setCoords){
        super(setArea, setOrient, setCoords);
    }

    /**
     * The method that is launched when the ICMazeActor enters a given ICMazeArea
     * (registration and placement of the ICMazeActor in the provided ICMazeArea)
     * @param area the ICMazeArea the ICMazeActor is entering
     * @param position the DiscretePosition at which the ICMazeactor is placed at
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setCurrentPosition(position.toVector());
        resetMotion();
        setOwnerArea(area);
    }
    /**
     * Whether the ICMazeActor blocks the cell it walks into
     * @return false, by default
     */
    public boolean takeCellSpace(){
        return false;
    }

    /**
     * Whether the ICMazeActor can proceed with cell interactions
     * @return true, by default
     */
    public boolean isCellInteractable(){return true;}

    /**
     * Whether the ICMazeActor can proceed with view interactions
     * @return false, by default
     */
    public boolean isViewInteractable(){return false;}

    /**
     * abstract method, which accepts any interaction by another actor, by allowing their interaction handler to proceed
     * @param v the other Interactor, which wants to interact with the Player
     * @param isCellInteraction whether this is a cell interaction
     */
    public abstract void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction);

    /**
     * method, which provides the DiscretePosition of the cell in front of the ICMazeActor, according to the provided Orientation
     * @param orient the orientation along which we want to have the next position
     * @return DiscreteCoordinates - the next position along the provided Orientation
     */
    protected DiscreteCoordinates getNextPosition(Orientation orient){
        DiscreteCoordinates pos = getCurrentMainCellCoordinates();
        DiscreteCoordinates newPos = getCurrentMainCellCoordinates();
        switch (orient){
            case UP -> newPos = new DiscreteCoordinates(pos.x, pos.y+1);
            case DOWN -> newPos = new DiscreteCoordinates(pos.x, pos.y-1);
            case RIGHT -> newPos = new DiscreteCoordinates(pos.x+1, pos.y);
            case LEFT -> newPos = new DiscreteCoordinates(pos.x-1, pos.y);
        }
        return newPos;
    }

    /**
     * method, which returns the cells that are occupied by the ICMazeActor
     * @returns List<DiscreteCoordinates> - the list containing the ICMazeActor's position
     */
    @Override
    public List<DiscreteCoordinates > getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    /**
     * method, which updates the ICMazeActor
     * @param deltaTime the time interval (double)
     */
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    /**
     * method damages this ICMazeActor by a certain amount of damage and makes him enter his immunity state
     * @param damage the damage inflicted to this ICMazeActor
     */
    public void damageActor(int damage){
        if (!recovering){
            health.decrease(damage);
            drawFrame = 0;
            recovering = true;
        }
    }

    /**
     * Setter which changes the ICMazeActor's recovery state
     * @param state the boolean state of the ICMazeActor's recovery
     */
    protected void setRecovery(boolean state){
        recovering = state;
    }

    /**
     * Getter which gets the ICMazeActor's recovery state
     * @return the boolean state of the ICMazeActor's recovery
     */
    protected boolean getRecovery(){return recovering;}

    /**
     * Initializes the Cooldown object of this ICMazeActor
     */
    protected void setCd(){
        cd = new Cooldown(health.STANDARD_COOLDOWN_TIME);
    }

    /**
     * Method which handles an ICMazeActor's immunity phase, setting its recovery attribute to false,
     * when the cooldown is over
     * @param deltaTime the time interval
     */
    protected void handleRecovery(float deltaTime){
        if (recovering){
            recovering = !cd.ready(deltaTime);
        }
        else {
            cd.reset();
        }
    }

    /**
     * Method which handles an ICMazeActor's animation, the display of its health bar when damaged and the
     * oscillating frame effect when in its immunity phase
     * @param UI the OrientedAnimation that will be drawn
     * @param canvas the Canvas on which the OrientedAnimation is drawn on
     */
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
