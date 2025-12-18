package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.Boss.ICMazeBoss;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.enemies.LogMonster;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

/**
 * Class, which represents the player object, which is controlled by the player's keyboard
 */
public class ICMazePlayer extends ICMazeActor implements Interactor {
    //constants
    private final static int ANIMATION_DURATION = 6;
    private final String prefix = "icmaze/player";
    private final int PICKAXE_ANIMATION_DURATION = 5;
    private final int MAX_DAMAGE = 1;
    private final Orientation[] orders = { Orientation.DOWN , Orientation.RIGHT , Orientation.UP, Orientation.LEFT };
    private final Orientation[] pickOrders = {Orientation.DOWN , Orientation.UP, Orientation.RIGHT , Orientation.LEFT};
    private final Vector anchor = new Vector(0, 0);
    private final Vector pickaxeAnchor = new Vector(-.5f, 0);
    private final static int MAX_LIFE = 4;

    //variables
    private KeyBindings.PlayerKeyBindings keys = KeyBindings.PLAYER_KEY_BINDINGS;
    private PlayerStates currentState; //current state of the player
    private static Keyboard keyboard;
    private OrientedAnimation UI;
    private OrientedAnimation pickaxeAnim;
    private ICMazePlayerInteractionHandler interactionHandler;
    private ArrayList<Integer> memeorizedKeys = new ArrayList<>(); //the list of keys collected by the player
    private boolean hasPickaxe; //becomes true, when the player collects the pickaxe
    private Portal currentPortal = null; //becomes not null, when the player steps into a portal
    private boolean attacking; //becomes true, when the player attacks with the pickaxe

    /**
     * enumeration, which provides all three states the player may enter
     */
    public enum PlayerStates{
        INTERACTING,
        IDLE,
        ATTACKING_WITH_PICKAXE,
    }

    /**
     * Constructor for the player class, which initializes the player's UI elements
     * @param setArea the area where the player will be created in
     * @param setOrient the default orientation of the player, during his creation
     * @param setCoords the coordinates at which the player will be created
     */
    public ICMazePlayer(Area setArea, Orientation setOrient, DiscreteCoordinates setCoords){
        super(setArea, setOrient, setCoords);
        keyboard = getOwnerArea().getKeyboard();
        //UI initialization
        pickaxeAnim = new OrientedAnimation(prefix+".pickaxe", PICKAXE_ANIMATION_DURATION , this , pickaxeAnchor , pickOrders , 4, 2, 2, 32, 32);
        UI = new OrientedAnimation(prefix , ANIMATION_DURATION , this , anchor , orders , 4, 1, 2, 16, 32, true);
        health = new Health(this, Transform.I.translated(0, 1.25f), MAX_LIFE , true);
        setCd(); //creation of the Cooldown timer
        interactionHandler = new ICMazePlayerInteractionHandler(); //the player's interaction handler, for view and cell interractions
        hasPickaxe = false;
        currentState = PlayerStates.IDLE; //default state of the player
    }

    /**
     * method, which oriends the player based on the pressed key and its associated orientation
     * @param key the key, which may be pressed or not
     * @param orient the orientation associated with the given key
     */
    private void displace(Button key, Orientation orient){
        //orientates and moves the character only if the key is pressed and the player isn't already moving
        if (key.isDown()){
            if (!isDisplacementOccurs()){
                orientate(orient);
                move(ANIMATION_DURATION);
            }
        }
    }

    /**
     * method, which draws the player on the provided canvas
     * @param canvas the canvas on which the player will be drawn
     */
    @Override
    public void draw(Canvas canvas){
        //the attacking animation is only played, when the player has attacked with his attacking
        if (!attacking){
            handleAnim(UI, canvas);
        }
        else {
            handleAnim(pickaxeAnim, canvas);
        }
    }

    /**
     * method, which draws the player on the provided canvas
     * @param deltaTime -time variation (double)
     */
    public void runAnim(float deltaTime){
        //handles the currently played animation based on whether the player is attacking with his pickaxe or not
        if (!pickaxeAnim.isCompleted() && attacking){
            pickaxeAnim.update(deltaTime);
        }
        else{
            attacking = false;
            pickaxeAnim.reset();//renews the pickaxe animation, when it comes to an end
            if (isDisplacementOccurs()){
                UI.update(deltaTime);
            }
            else{
                UI.reset(); //renews the walking animation, when it comes to an end
            }
        }
    }

    /**
     * method, which updates the player object, based on player input
     * @param deltaTime -time variation (double)
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (!ICMaze.runningDialog() && !ICMaze.isGamePaused()){ //met seulement le joueur à jour, s'il n'y a pas de dialogue
            handleRecovery(deltaTime);
            switch (currentState){
                case IDLE -> {
                    //movement, attack and interraction key handling
                    displace(keyboard.get(keys.left()), Orientation.LEFT);
                    displace(keyboard.get(keys.up()), Orientation.UP);
                    displace(keyboard.get(keys.down()), Orientation.DOWN);
                    displace(keyboard.get(keys.right()), Orientation.RIGHT);
                    if (keyboard.get(keys.pickaxe()).isPressed() && hasPickaxe && !attacking){
                        currentState = PlayerStates.ATTACKING_WITH_PICKAXE;
                    }
                    if (keyboard.get(keys.interact()).isPressed()) {
                        currentState = PlayerStates.INTERACTING;
                    }
                    UI.update(deltaTime);
                    runAnim(deltaTime);
                }
                case INTERACTING -> {
                    //resets the player's state, when he lets go of the key
                    if (!keyboard.get(keys.interact()).isDown()) {
                        currentState = PlayerStates.IDLE;
                    }
                }
                case ATTACKING_WITH_PICKAXE -> {
                    //resets the player's state, and turns the attacking attribute true, when the player attacks
                    currentState = PlayerStates.IDLE;
                    attacking = true;
                }
            }
        }
    }

    /**
     * method, which checks whether the player currently owns the needed key
     * @param key the key that is needed for progression into the next area
     */
    public boolean checkKey(int key){return memeorizedKeys.indexOf(key) != -1;}

    /**
     * getter, which returns which portal the player steps into (null, unless the player steps into a portal)
     * @return Portal, the portal in which the player may have stepped in
     */
    public Portal getCurrentPortal() {return currentPortal;}

    /**
     * resets the player's currentPortal attribute, when the player has been teleported into the next area
     */
    public void clearCurrentPortal() {currentPortal = null;}

    /**
     * method, which damages the player by a given amount of damage
     * @param damage the amount of damage inflicted to the player (int)
     */
    public void beAttacked(int damage){
        damageActor(damage);
    } //calls the damageActor method of the super-class

    /**
     * method, which returns the amount of health the player has.
     * This makes the game reset, when the player's health is non-positive
     * @return int - the player's current amount of health
     */
    public int getHealth(){return health.getHealth();}

    /**
     * method, which returns the cell directly in front of the player
     * @return List<DiscreteCoordinates> - the list containing only the cell right in front of the player
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    /**
     * method, which returns whether the player can be interacted with through view interactions
     * @return true, as the player is always interactable through view interactions
     */
    @Override
    public boolean isViewInteractable() {return true;}

    /**
     * method, which returns whether the player can do view interactions himself
     * @return true, as the player can always interact through view interactions
     */
    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    /**
     * method, which returns whether the player can object is currently able to do a view interaction
     * @return boolean - whether the player is in a state, which allows for view interactions
     */
    @Override
    public boolean wantsViewInteraction() {
        return currentState == PlayerStates.INTERACTING || currentState == PlayerStates.ATTACKING_WITH_PICKAXE;
    }
    /**
     * method, which accepts any interaction by another actor, by allowing their interaction handler to proceed
     * @param v the other Interactor, which wants to interact with the Player
     * @param isCellInteraction whether this is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
    /**
     * method, which asks another actor, if it can proceed with an interaction with that other actor
     * @param other the other Interactable, with which the player wants to interact with
     * @param isCellInteraction whether this is a cell interaction
     */
    @Override
    public void interactWith(Interactable other , boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler , isCellInteraction);
    }

    /**
     * This class is the player's interaction handling class,
     * which handles interactions between the player and specific actors
     */
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor{
        /**
         * method which handles interactions between the player and the Pickaxe object
         * @param pickaxe the Pickaxe object
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction){
            if (!pickaxe.isCollected()){
                hasPickaxe = true; //collection and removal of the pickaxe from the current area
                ((ICMazeArea) getOwnerArea()).removeItem(pickaxe);
            }
        };

        /**
         * method which handles interactions between the player and the Heart object
         * @param heart the Heart object
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(Heart heart, boolean isCellInteraction){
            if (!heart.isCollected()){ //heals the player, if his health is below maximal. It then deletes the heart
                if (health.getHealth() < MAX_LIFE){
                    health.increase(1);
                    drawFrame = 0;
                    setRecovery(true);
                }
                ((ICMazeArea) getOwnerArea()).removeItem(heart);
            }
        };

        /**
         * method which handles interactions between the player and the Key object
         * @param key the Key object
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(Key key, boolean isCellInteraction){
            if (!key.isCollected()){
                //if the key's ID, is equal to -1, it means that the player has collected the boss' key, leading to victory
                if (key.getID() == -1){
                    ((ICMazeArea) getOwnerArea()).setVictory();
                }
                //adds the key's ID to the player's collected keys, before removing the graphic key from the game
                memeorizedKeys.add(key.getID());
                ((ICMazeArea) getOwnerArea()).removeItem(key);
            }
        };

        /**
         * method which handles interactions between the player and the Rock object
         * @param rock the Rock object
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(Rock rock, boolean isCellInteraction){
            if (!isCellInteraction && currentState == PlayerStates.ATTACKING_WITH_PICKAXE){
                rock.beAttacked(MAX_DAMAGE); //damages the rock, when the player attacks
            }
        };

        /**
         * method which handles interactions between the player and the ICMazeBoss object
         * @param boss the Boss object
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(ICMazeBoss boss, boolean isCellInteraction){
            if (!isCellInteraction && currentState == PlayerStates.ATTACKING_WITH_PICKAXE){
                boss.beAttacked(MAX_DAMAGE); //damages the boss, when it is hit by the player
            }
        };

        /**
         * method which handles interactions between the player and the LogMonster object
         * @param logMonster the LogMonster object
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(LogMonster logMonster, boolean isCellInteraction){
            if (!isCellInteraction && currentState == PlayerStates.ATTACKING_WITH_PICKAXE){
                logMonster.beAttacked(MAX_DAMAGE); //damages the logmonster by a certain amount of damage, when attacked
            }
        };

        /**
         * method which handles interactions between the player and the Portal object
         * @param portal the Portal object
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (currentState == PlayerStates.INTERACTING) {
                /*
                 * when the player proceeds with a view interaction with the portal, checks whether the player possesses
                 * checks whether the player possesses the needed key to open this portal
                 */
                int neededKey = portal.getKeyId();
                if (neededKey == Portal.NO_KEY_ID || checkKey(neededKey)) {
                    portal.setState(PortalState.OPEN);
                }else {
                    ICMaze.noKeyDialog();
                }
            }
            //if the player steps into the portal instead, sets his currentPortal attribute to this portal
            if (isCellInteraction && portal.getState() == PortalState.OPEN) {
                currentPortal = portal;
            }
        }
    }
}
