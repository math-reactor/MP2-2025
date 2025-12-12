package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.Boss.ICMazeBoss;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.ICMazeBehaviour;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Sprite;
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

public class ICMazePlayer extends ICMazeActor implements Interactor {
    public enum PlayerStates{
        INTERACTING,
        IDLE,
        ATTACKING_WITH_PICKAXE,
    }
    private final static int ANIMATION_DURATION = 6;
    private final String prefix = "icmaze/player";
    private final int PICKAXE_ANIMATION_DURATION = 5;
    private final int MAX_DAMAGE = 1;
    final Orientation[] orders = { Orientation.DOWN , Orientation.RIGHT , Orientation.UP, Orientation.LEFT };
    final Orientation[] pickOrders = {Orientation.DOWN , Orientation.UP, Orientation.RIGHT , Orientation.LEFT};
    final Vector anchor = new Vector(0, 0);
    final Vector pickaxeAnchor = new Vector(-.5f, 0);
    private final static int MAX_LIFE = 3;


    private KeyBindings.PlayerKeyBindings keys = KeyBindings.PLAYER_KEY_BINDINGS;
    private PlayerStates currentState;
    private static Keyboard keyboard;
    private OrientedAnimation UI;
    private OrientedAnimation pickaxeAnim;
    private ICMazePlayerInteractionHandler interactionHandler;
    private ArrayList<Integer> memeorizedKeys = new ArrayList<>();
    private boolean hasPickaxe;
    private Portal currentPortal = null;
    private boolean attacking;

    //constructor
    public ICMazePlayer(Area setArea, Orientation setOrient, DiscreteCoordinates setCoords){
        super(setArea, setOrient, setCoords);
        keyboard = getOwnerArea().getKeyboard();
        pickaxeAnim = new OrientedAnimation(prefix+".pickaxe", PICKAXE_ANIMATION_DURATION , this , pickaxeAnchor , pickOrders , 4, 2, 2, 32, 32);
        UI = new OrientedAnimation(prefix , ANIMATION_DURATION , this , anchor , orders , 4, 1, 2, 16, 32, true);
        currentState = PlayerStates.IDLE;
        interactionHandler = new ICMazePlayerInteractionHandler();
        hasPickaxe = false;
        health = new Health(this, Transform.I.translated(0, 1.25f), MAX_LIFE , true);
        setCd();
    }
    private void displace(Button key, Orientation orient){
        //moves the character
        if (key.isDown()){
            if (!isDisplacementOccurs()){
                orientate(orient);
                move(ANIMATION_DURATION);
            }
        }
    }
    public void draw(Canvas canvas){
        if (!attacking){
            handleAnim(UI, canvas);
        }
        else {
            handleAnim(pickaxeAnim, canvas);
        }
    }
    public void runAnim(float deltaTime){
        //handles the currently played animation
        if (!pickaxeAnim.isCompleted() && attacking){
            pickaxeAnim.update(deltaTime);
        }
        else{
            attacking = false;
            pickaxeAnim.reset();
            if (isDisplacementOccurs()){
                UI.update(deltaTime);
            }
            else{
                UI.reset();
            }
        }
    }
    public void update(float deltaTime) {
        handleRecovery(deltaTime);
        switch (currentState){
            case IDLE -> {
                //movement
                displace(keyboard.get(keys.left()), Orientation.LEFT);
                displace(keyboard.get(keys.up()), Orientation.UP);
                displace(keyboard.get(keys.down()), Orientation.DOWN);
                displace(keyboard.get(keys.right()), Orientation.RIGHT);
                if (keyboard.get(keys.pickaxe()).isPressed() && ownsPickaxe() && !attacking){
                    currentState = PlayerStates.ATTACKING_WITH_PICKAXE;
                }
                if (keyboard.get(keys.interact()).isPressed()) {
                    currentState = PlayerStates.INTERACTING;
                }
                UI.update(deltaTime);
                runAnim(deltaTime);
            }
            case INTERACTING -> {
                if (!keyboard.get(keys.interact()).isDown()) {
                    currentState = PlayerStates.IDLE;
                }
            }
            case ATTACKING_WITH_PICKAXE -> {
                currentState = PlayerStates.IDLE;
                attacking = true;
            }
        }
        super.update(deltaTime);
    }
    public boolean ownsPickaxe(){return hasPickaxe;}
    public boolean checkKey(int key){return memeorizedKeys.indexOf(key) != -1;}
    public Portal getCurrentPortal() {return currentPortal;}
    public void clearCurrentPortal() {currentPortal = null;}

    public void beAttacked(int damage){
        if (!getRecovery()){
            health.decrease(damage);
            drawFrame = 0;
            setRecovery(true);
        }
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return currentState == PlayerStates.INTERACTING || currentState == PlayerStates.ATTACKING_WITH_PICKAXE;
    }

    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
    @Override
    public void interactWith(Interactable other , boolean isCellInteraction) {
        if (!isCellInteraction){
            //System.out.println(other.getClass());
        }
        other.acceptInteraction(interactionHandler , isCellInteraction);
    }
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor{
        //handles the interactions between the player and other actors
        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction){
            if (!pickaxe.isCollected()){
                hasPickaxe = true;
                ((ICMazeArea) getOwnerArea()).removeItem(pickaxe);
            }
        };
        public void interactWith(Heart heart, boolean isCellInteraction){
            if (!heart.isCollected()){
                if (health.getHealth() < MAX_LIFE){
                    health.increase(1);
                    drawFrame = 0;
                    setRecovery(true);
                }
                ((ICMazeArea) getOwnerArea()).removeItem(heart);
            }
        };
        public void interactWith(Key key, boolean isCellInteraction){
            if (!key.isCollected()){
                memeorizedKeys.add(key.getID());
                if (key.getID() == -1){
                    ((BossArea) getOwnerArea()).victory();
                }
                ((ICMazeArea) getOwnerArea()).removeItem(key);
            }
        };
        public void interactWith(Rock rock, boolean isCellInteraction){
            if (!isCellInteraction && currentState == PlayerStates.ATTACKING_WITH_PICKAXE){
                rock.damage(MAX_DAMAGE);
            }
        };
        public void interactWith(ICMazeBoss boss, boolean isCellInteraction){
            if (!isCellInteraction && currentState == PlayerStates.ATTACKING_WITH_PICKAXE){
                boss.beAttacked(MAX_DAMAGE);
            }
        };
        public void interactWith(Portal portal, boolean isCellInteraction) {
            if (currentState == PlayerStates.INTERACTING) {
                int neededKey = portal.getKeyId();
                if (neededKey == Portal.NO_KEY_ID || checkKey(neededKey)) {
                    portal.setState(PortalState.OPEN);
                }
            }
            if (isCellInteraction && portal.getState() == PortalState.OPEN) {
                currentPortal = portal;
            }
        }
    }
}
