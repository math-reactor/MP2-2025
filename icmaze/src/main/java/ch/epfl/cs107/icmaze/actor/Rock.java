package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.icmaze.handler.Damageable;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Class, which represents a rock object, which is used as a part of a wall, to create the mazes in the MazeAreas
 */
public class Rock extends AreaEntity implements Damageable {
    //Constants
    private final int ANIMATION_DURATION = 24;
    private final static int MAX_LIFE = 3;
    //Animation variables
    private Sprite UI;
    private Animation poof;
    private boolean destroyed = false;
    private boolean recovering = false;
    private Health health = new Health(this, Transform.I.translated(0, 0.25f), MAX_LIFE , false);
    private Cooldown cd = new Cooldown(health.STANDARD_COOLDOWN_TIME);
    private int drawFrame;

    /**
     * Constructor for the rock class, which initializes the rock's UI, and its destruction animation
     * @param area the area in which the boss is created
     * @param pos the position at which the rock is created
     */
    public Rock(Area area, DiscreteCoordinates pos){
        super(area, Orientation.DOWN, pos);
        UI = new Sprite("rock.2", 1f, 1f, this);
        poof = new Animation("icmaze/vanish", 7, 2, 2, this , 32, 32, new Vector(-0.5f, 0.0f), ANIMATION_DURATION/7, false);
        enterArea(area, pos);
    }

    /**
     * method, which allows the rock to be placed in a given area
     * @param area the area in which the rock is placed
     * @param position the position at which the rock is placed
     */
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setCurrentPosition(position.toVector());
    }

    /**
     * method, which damages the rock by a certain amount of damage
     * @param damage the area in which the rock is placed
     */
    public void beAttacked(int damage){
        if (!recovering){ //only damages the rock, when the rock is not in its regenerating state
            health.decrease(damage);
            drawFrame = 0;
            recovering = true;
        }
    }

    /**
     * method, which handles the destruction of the rock, when its HP reaches 0
     */
    private void handleRockDestruction(){
        //destroys the rock, and has a probability to give a heart
        destroyed = true;
        DiscreteCoordinates coords = getCurrentMainCellCoordinates();
        ((MazeArea) getOwnerArea()).makePointWalkable(coords);
        ((ICMazeArea) getOwnerArea()).removeItem(this);
        //Each destroyed wall has a 1/3 chance, to spawn a Heart in its place
        int randVal = RandomGenerator.rng.nextInt(3);
        if (randVal == 0){
            ((ICMazeArea) getOwnerArea()).replaceWallByHeart(coords);
        }
    }

    @Override
    /**
     * method, which draws the rock on the window's canvas
     * @param canvas the canvas on which the rock is drawn on
     */
    public void draw(Canvas canvas) {
        if (health.getHealth() > 0){
            if (recovering){ //handles the display of the rock, when it has been damaged
                if (drawFrame % 3 == 0){ //handles the oscillating frames, when the rock is hit
                    UI.draw(canvas);
                }
                drawFrame += 1;
                health.draw(canvas);
            }
            else { //handles the display of the rock in its default state
                UI.draw(canvas);
            }
        }
        else{
            //draws the destruction cloud, when the rock's health hits 0
            if (!destroyed){
                poof.draw(canvas);
            }
            if (poof.isCompleted() && !destroyed){
                //clears the rock, when the cloud is gone
                handleRockDestruction();
            }
        }
    }

    @Override
    /**
     * method, which updates the rock's state
     * @param deltaTime time variation
     */
    public void update(float deltaTime) {
        //handles the rock's imunity and regeneration phase, after having been hit
        if (recovering){
            recovering = !cd.ready(deltaTime); //the rock recovers until a timer is over
        }
        else {
            cd.reset(); //reset of the timer, when the rock's immunity phase is over
        }
        if (health.getHealth() <= 0 && !poof.isCompleted()){
            poof.update(deltaTime); //updates the cloud's animation, when the rock is destroyed
        }
        super.update(deltaTime);
    }
    @Override
    /**
     * method, which returns the cells that are occupied by the rock
     * @returns List<DiscreteCoordinates> - the list containing the rock's position
     */
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    @Override
    /**
     * method, which returns whether the rock occupies the cell it is located in. Only true, if it hasn't been destroyed yet
     * @returns boolean - indicates whether it can be walked on or not
     */
    public boolean takeCellSpace() {return !destroyed;}

    @Override
    /**
     * method, which returns whether the rock can be interacted with through cell interractions
     * @returns boolean - indicates whether it can respond to cell interractions - always false
     */
    public boolean isCellInteractable() {return false;}

    @Override
    /**
     * method, which returns whether the rock can be interacted with through view interractions (whether it can be attacked)
     * @returns boolean - indicates whether it can respond to view interractions (Only true, if its health is positive)
     */
    public boolean isViewInteractable() {return health.getHealth() > 0;}

    @Override
    /**
     * method, which only accepts incoming interraction requests, allowing the interractor to proceed with the interraction
     */
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
