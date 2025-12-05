package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.ICMazeBehaviour;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
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
    private final static int ANIMATION_DURATION = 8;
    private final String prefix = "icmaze/player";
    private final int PICKAXE_ANIMATION_DURATION = 4;
    final Orientation[] orders = { Orientation.DOWN , Orientation.RIGHT , Orientation.UP, Orientation.LEFT };
    final Vector anchor = new Vector(0, 0);

    private KeyBindings.PlayerKeyBindings keys = KeyBindings.PLAYER_KEY_BINDINGS;
    private PlayerStates currentState;
    private static Keyboard keyboard;
    private OrientedAnimation UI;
    private ICMazePlayerInteractionHandler interactionHandler;
    private ArrayList<Integer> memeorizedKeys = new ArrayList<>();
    private boolean hasPickaxe;
    private Portal currentPortal;
    //constructor
    public ICMazePlayer(Area setArea, Orientation setOrient, DiscreteCoordinates setCoords){
        super(setArea, setOrient, setCoords);
        keyboard = getOwnerArea().getKeyboard();
        UI = new OrientedAnimation(prefix , ANIMATION_DURATION , this , anchor , orders , 4, 1, 2, 16, 32, true);
        currentState = PlayerStates.IDLE;
        interactionHandler = new ICMazePlayerInteractionHandler();
        hasPickaxe = false;
    }
    private void displace(Button key, Orientation orient){
        if (key.isDown()){
            if (!isDisplacementOccurs()){
                orientate(orient);
                move(ANIMATION_DURATION);
            }
        }
    }
    public void draw(Canvas canvas){
        UI.draw(canvas);
    }
    public void handleAnim(float deltaTime){
        if (isDisplacementOccurs()){
            UI.update(deltaTime);
        }
        else{
            UI.reset();
        }
    }
    public void update(float deltaTime) {
        final Vector anchor = new Vector(0, 0);
        final Orientation[] orders = { Orientation.DOWN , Orientation.RIGHT , Orientation.UP, Orientation.LEFT };
        switch (currentState){
            case IDLE -> {
                displace(keyboard.get(keys.left()), Orientation.LEFT);
                displace(keyboard.get(keys.up()), Orientation.UP);
                displace(keyboard.get(keys.down()), Orientation.DOWN);
                displace(keyboard.get(keys.right()), Orientation.RIGHT);
                if (keyboard.get(keys.pickaxe()).isPressed() && ownsPickaxe()){

                }
                if (keyboard.get(keys.interact()).isPressed()) {
                    currentState = PlayerStates.INTERACTING;
                }
                handleAnim(deltaTime);
                UI.update(deltaTime);
            }
            case INTERACTING -> {
                if (!keyboard.get(keys.interact()).isDown()) {
                    currentState = PlayerStates.IDLE;
                }
            }
        }
        super.update(deltaTime);
    }
    public boolean takeCellSpace(){
        return true;
    }
    public boolean ownsPickaxe(){return true;}
    public boolean checkKey(int key){return memeorizedKeys.indexOf(key) != -1;}
    public Portal getCurrentPortal() {return currentPortal;}
    public void clearCurrentPortal() {currentPortal = null;}

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
        return currentState == PlayerStates.INTERACTING;
    }

    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
    @Override
    public void interactWith(Interactable other , boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler , isCellInteraction);
    }
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor{
        //gère les interactions entre le joueur et les autres choses
        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction){
            if (!pickaxe.isCollected()){
                hasPickaxe = true;
                ((ICMazeArea) getOwnerArea()).removeItem(pickaxe);
            }
        };
        public void interactWith(Heart heart, boolean isCellInteraction){
            if (!heart.isCollected()){
                ((ICMazeArea) getOwnerArea()).removeItem(heart);
            }
        };
        public void interactWith(Key key, boolean isCellInteraction){
            if (!key.isCollected()){
                memeorizedKeys.add(key.getID());
                ((ICMazeArea) getOwnerArea()).removeItem(key);
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
