package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.ICMaze;
import ch.epfl.cs107.icmaze.KeyBindings;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
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

    // ------------------ VIE DU JOUEUR (pdf p.31) ------------------
    private static final int MAX_LIFE = 5; // constante commune à tous les joueurs
    private int currentLife = MAX_LIFE;

    /** Inflige des dégâts au joueur. S’il tombe à 0 → reset de la partie. */
    public void takeDamage(int damage) {
        if (damage <= 0) return;

        currentLife -= damage;
        if (currentLife <= 0) {
            currentLife = 0;
            // Mort du joueur : on reset la partie (cf. énoncé p.31)
            Area owner = getOwnerArea();
            if (owner != null && owner.getGame() instanceof ICMaze icMaze) {
                icMaze.reset();
            }
        }
    }

    /** Soigne le joueur (sans dépasser MAX_LIFE). */
    private void heal(int amount) {
        if (amount <= 0) return;
        currentLife = Math.min(MAX_LIFE, currentLife + amount);
    }

    // ------------------ ÉTATS / ANIMATIONS ------------------
    public enum PlayerStates {
        INTERACTING,
        IDLE,
        ATTACKING_WITH_PICKAXE,
    }

    private static final int ANIMATION_DURATION = 6;
    private static final int PICKAXE_ANIMATION_DURATION = 5;

    private final String prefix = "icmaze/player";
    private final Orientation[] orders =
            {Orientation.DOWN, Orientation.RIGHT, Orientation.UP, Orientation.LEFT};
    private final Orientation[] pickOrders =
            {Orientation.DOWN, Orientation.UP, Orientation.RIGHT, Orientation.LEFT};
    private final Vector anchor = new Vector(0, 0);
    private final Vector pickaxeAnchor = new Vector(-.5f, 0);

    private KeyBindings.PlayerKeyBindings keys = KeyBindings.PLAYER_KEY_BINDINGS;
    private PlayerStates currentState;
    private static Keyboard keyboard;
    private OrientedAnimation UI;
    private OrientedAnimation pickaxeAnim;
    private ICMazePlayerInteractionHandler interactionHandler;
    private ArrayList<Integer> memeorizedKeys = new ArrayList<>();
    private boolean hasPickaxe;
    private Portal currentPortal = null;
    private boolean attacking = false;

    // ------------------ CONSTRUCTEUR ------------------
    public ICMazePlayer(Area setArea, Orientation setOrient, DiscreteCoordinates setCoords) {
        super(setArea, setOrient, setCoords);
        keyboard = getOwnerArea().getKeyboard();
        pickaxeAnim = new OrientedAnimation(
                prefix + ".pickaxe",
                PICKAXE_ANIMATION_DURATION,
                this,
                pickaxeAnchor,
                pickOrders,
                4, 2, 2,
                32, 32
        );
        UI = new OrientedAnimation(
                prefix,
                ANIMATION_DURATION,
                this,
                anchor,
                orders,
                4, 1, 2,
                16, 32,
                true
        );
        currentState = PlayerStates.IDLE;
        interactionHandler = new ICMazePlayerInteractionHandler();
        hasPickaxe = false;
        currentLife = MAX_LIFE; // le joueur naît avec la vie max
    }

    private void displace(Button key, Orientation orient) {
        if (key.isDown()) {
            if (!isDisplacementOccurs()) {
                orientate(orient);
                move(ANIMATION_DURATION);
            }
        }
    }

    // ------------------ DESSIN / ANIM ------------------
    @Override
    public void draw(Canvas canvas) {
        if (!attacking) {
            UI.draw(canvas);
        } else {
            pickaxeAnim.draw(canvas);
        }
    }

    public void handleAnim(float deltaTime) {
        if (!pickaxeAnim.isCompleted() && attacking) {
            pickaxeAnim.update(deltaTime);
        } else {
            attacking = false;
            pickaxeAnim.reset();
            if (isDisplacementOccurs()) {
                UI.update(deltaTime);
            } else {
                UI.reset();
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        switch (currentState) {
            case IDLE -> {
                displace(keyboard.get(keys.left()), Orientation.LEFT);
                displace(keyboard.get(keys.up()), Orientation.UP);
                displace(keyboard.get(keys.down()), Orientation.DOWN);
                displace(keyboard.get(keys.right()), Orientation.RIGHT);
                if (keyboard.get(keys.pickaxe()).isPressed()
                        && ownsPickaxe()
                        && !attacking) {
                    currentState = PlayerStates.ATTACKING_WITH_PICKAXE;
                }
                if (keyboard.get(keys.interact()).isPressed()) {
                    currentState = PlayerStates.INTERACTING;
                }
                UI.update(deltaTime);
                handleAnim(deltaTime);
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

    // ------------------ API UTILISATEUR ------------------
    public boolean ownsPickaxe() {
        return hasPickaxe;
    }

    public boolean checkKey(int key) {
        return memeorizedKeys.indexOf(key) != -1;
    }

    public Portal getCurrentPortal() {
        return currentPortal;
    }

    public void clearCurrentPortal() {
        currentPortal = null;
    }

    // ------------------ INTERACTOR ------------------
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(
                getCurrentMainCellCoordinates().jump(getOrientation().toVector())
        );
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return currentState == PlayerStates.INTERACTING
                || currentState == PlayerStates.ATTACKING_WITH_PICKAXE;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler, isCellInteraction);
    }

    // ------------------ GESTIONNAIRE D’INTERACTIONS ------------------
    private class ICMazePlayerInteractionHandler implements ICMazeInteractionVisitor {

        public void interactWith(Pickaxe pickaxe, boolean isCellInteraction) {
            if (!pickaxe.isCollected()) {
                hasPickaxe = true;
                ((ICMazeArea) getOwnerArea()).removeItem(pickaxe);
            }
        }

        public void interactWith(Heart heart, boolean isCellInteraction) {
            if (!heart.isCollected()) {
                heal(1); // +1 PV, sans dépasser MAX_LIFE
                ((ICMazeArea) getOwnerArea()).removeItem(heart);
            }
        }

        public void interactWith(Key key, boolean isCellInteraction) {
            if (!key.isCollected()) {
                memeorizedKeys.add(key.getID());
                ((ICMazeArea) getOwnerArea()).removeItem(key);
            }
        }

        public void interactWith(Rock rock, boolean isCellInteraction) {
            if (!isCellInteraction && rock.canBeAttacked()) {
                rock.damage();
            }
        }

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