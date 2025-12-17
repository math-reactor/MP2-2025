package ch.epfl.cs107.icmaze.actor.Boss;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Class, which represents the boss object, which is fought by the player in the BossArea
 */
public class ICMazeBoss extends ICMazeActor {
    //constants
    private final static Vector anchor = new Vector(-0.5f, 0);
    private final static Orientation[] orders = {Orientation.DOWN , Orientation.RIGHT , Orientation.UP, Orientation.LEFT};
    private final static int[][] teleportPositions = {{4, 1}, {8, 4}, {4, 8}, {1, 4}};
    private final double SHOOTINTERVAL = 5;
    private final int ANIMATION_DURATION = 60;
    private final int MAX_LIFE = 5;

    //UI variables
    private OrientedAnimation UI;
    private Animation poof;

    //other variables
    private boolean angered = false;
    private double timeInterval = 0;
    private boolean destroyed = false;

    /**
     * Constructor for the ICMazeBoss class, which initializes the boss' UI elements
     * @param setArea the area where the boss will be created in
     */
    public ICMazeBoss(Area setArea){
        //the boss' default spawn location is the middle of BossArea. It is spawned with a downward orientation
        super(setArea, Orientation.DOWN, new DiscreteCoordinates(setArea.getWidth()/2, setArea.getHeight()/2));
        UI = new OrientedAnimation("icmaze/boss", ANIMATION_DURATION/4, this , anchor , orders , 3, 2, 2, 32, 32, true);
        poof = new Animation("icmaze/vanish", 7, 2, 2, this , 32, 32, new Vector(-0.5f, 0.0f), ANIMATION_DURATION/7, false);
        health = new Health(this, Transform.I.translated(0, 1.25f), MAX_LIFE , false);
        setCd();
    }

    /**
     * method, which enables the boss to teleport randomly between the four possible positions from teleportPositions
     */
    private void teleportRandom(){
        int Chosen;
        Orientation randomOr;
        //chooses a random position, which is different from the current position
        do{
            Chosen = RandomGenerator.rng.nextInt(4);
            randomOr = orders[Chosen].opposite();
        } while(getOrientation() == randomOr); //each of the four positions is characterized by a given orientation
        int[] chosenPos = teleportPositions[Chosen];
        Area currentArea = getOwnerArea();
        //handles the teleporting aspect
        exitArea();
        orientate(randomOr); //the boss' orientation is the opposite of its location in the area
        enterArea(currentArea, new DiscreteCoordinates(chosenPos[0], chosenPos[1]));
    }

    /**
     * method, which enables the boss to exit the BossAre, to make him move to a different position
     */
    public void exitArea() {
        getOwnerArea().leaveAreaCells(this, getCurrentCells());
        getOwnerArea().unregisterActor(this);
    }

    /**
     * method, which allows the player to damage the boss
     * @param damage the damage inflicted to the boss (int)
     */
    @Override
    public void beAttacked(int damage){
        if (health.getHealth() > 1 && !getRecovery()){
            // > 1, to prevent the key from being the key from being teleported away, when the boss is damaged
            teleportRandom();
            UI.reset();
        }
        if (angered){ //damages the boss, only when it is actvated
            damageActor(damage);
        }
        angered = true;
    }

    /**
     * method, which returns whether the boss prevents the player from walking through it
     * @return true, as the boss can't be walked through
     */
    @Override
    public boolean takeCellSpace(){
        return true;
    }

    /**
     * method, which returns whether the boss can be interacted with through view interactions
     * @return true, as the boss can only be attacked with view interactions
     */
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    /**
     * method, which allows the boss to launch foreball walls at the player,
     * with only one spot free spot to allow him to pass through
     */
    private void attack(){
        int[] commonPos = {-1,-1}; //provides the one coodinate, which all fireballs will have in common
        switch (getOrientation()) {
            case DOWN -> commonPos = new int[]{-1, 7};//Boss up north - all fireballs are spawned at y = 7
            case UP -> commonPos = new int[]{-1, 2};//Boss down south - all fireballs are spawned at y = 2
            case RIGHT -> commonPos = new int[]{2, -1};//Boss up north - all fireballs are spawned at x = 2
            case LEFT -> commonPos = new int[]{7, -1};//Boss up north - all fireballs are spawned at x = 7
        }
        //the -1 component of each commonPos denotes, which one of the two the fireballs do not have in common
        int randomHole = RandomGenerator.rng.nextInt(1, 9); //the position of the free spot
        for (int i = 1; i < 9; i++){ //creates 7 fireballs, with one free spot for the player to pass through
            int[] newCoords = commonPos.clone();
            if (i != randomHole){
                for (int coord = 0; coord < 2; coord++){
                    if (commonPos[coord] < 0){
                        newCoords[coord] = i; //updates the one coordinate, which the fireballs do not have in common
                    }
                }
                //creates the fireball object in the game
                DiscreteCoordinates creationPos = new DiscreteCoordinates(newCoords[0], newCoords[1]);
                BossProjectile newProj = new FireProjectile(getOwnerArea(), getOrientation(), creationPos);
                //Water projectiles are not sigma
                ((ICMazeArea) getOwnerArea()).addItem(newProj);
            }
        }
    }

    /**
     * method, which handles the updating of the boss
     * @param deltaTime the time variation
     */
    @Override
    public void update(float deltaTime) {
        if (health.getHealth() > 0){
            if (angered){ //the boss will be activated, after first being hit
                handleRecovery(deltaTime);
                timeInterval += deltaTime; //handles the timing of the boss' attacks
                if (timeInterval >= SHOOTINTERVAL){
                    timeInterval = 0;
                    attack();
                }
            }
            UI.update(deltaTime); //updates the boss' UI
        }
        else {
            if (!destroyed){
                poof.update(deltaTime); //draws the dust cloud in the boss' position after the boss has been defeated
                if (poof.isCompleted() && !destroyed){
                    destroyed = true;
                    //finally eliminates the boss, when the cloud is gone
                    ((BossArea) getOwnerArea()).killBoss();
                }
            }
        }
    }

    /**
     * method, updates the boss' animation based on its current state
     * @param canvas the canvas on which the boss is drawn upon
     */
    @Override
    public void draw(Canvas canvas) {
        if (health.getHealth() > 0){
            handleAnim(UI, canvas); //default animation when it is still at full health
        }
        else{
            if (!destroyed){
                poof.draw(canvas); //cloud animation, after having been defeated
            }
        }
    }
    /**
     * method, allows other Interactors to interact with the boss
     * @param v the other Interactor, which wants to interact with the boss
     * @param isCellInteraction A boolean, which gives whether this is a cell interaction or not
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
