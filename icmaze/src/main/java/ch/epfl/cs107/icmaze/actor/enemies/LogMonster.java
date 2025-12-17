package ch.epfl.cs107.icmaze.actor.enemies;

import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Path;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LogMonster extends PathFinderEnemy implements Interactor {

    private static final int MAX_LIFE = 2;          // PV max
    private static final int PERCEPTION_RADIUS = 5; // rayon de perception
    private static final int DAMAGE_TO_PLAYER = 1;  // dégâts infligés au joueur

    //orientations
    final Orientation[] targetedMovingOrders = new Orientation []{ Orientation.DOWN , Orientation.UP, Orientation.RIGHT , Orientation.LEFT};
    final Orientation[] movingOrders = new Orientation []{ Orientation.DOWN , Orientation.UP, Orientation.RIGHT , Orientation.LEFT};
    final Orientation[] sleepingOrders = new Orientation []{ Orientation.DOWN , Orientation.LEFT , Orientation.UP, Orientation.RIGHT};

    private static final float REORIENT_TIME = 0.75f;
    private static final float STATE_TRANSITION_TIME = 3f;
    private final int ANIMATION_DURATION = 30;

    public enum State { SLEEPING, WANDERING, CHASING }

    private State state;
    private ICMazePlayer memorizedTarget;
    private boolean destroyed = false;

    private float reorientTimer;
    private float stateTimer;

    //Animations
    private OrientedAnimation targetedMoveAnim;
    private OrientedAnimation moveAnim;
    private OrientedAnimation sleepAnim;
    private Animation poof;
    private Path graphicPath;
    private LogMonsterInteractionHandler interactionHandler;
    private Queue<Orientation> currentPath;

    public LogMonster(ICMazeArea area, Orientation orientation, DiscreteCoordinates position, State initialState) {
        super(area, orientation, position, PERCEPTION_RADIUS);
        health = new Health(this, Transform.I.translated(0, 1.25f), MAX_LIFE , false);
        setCd();
        this.state = initialState;
        interactionHandler = new LogMonsterInteractionHandler();
        targetedMoveAnim =  new OrientedAnimation("icmaze/logMonster", ANIMATION_DURATION/3, this , new Vector(-0.5f, 0.25f), targetedMovingOrders , 4, 2, 2, 32, 32, true);
        moveAnim =  new OrientedAnimation("icmaze/logMonster_random", ANIMATION_DURATION/3, this , new Vector(-0.5f, 0.25f), movingOrders , 4, 2, 2, 32, 32, true);
        sleepAnim = new OrientedAnimation("icmaze/logMonster.sleeping", ANIMATION_DURATION/3, this , new Vector(-0.5f, 0.25f), sleepingOrders, 1, 2, 2, 32, 32, true);
        poof = new Animation("icmaze/vanish", 7, 2, 2, this , 32, 32, new Vector(-0.5f, 0.0f), ANIMATION_DURATION/7, false);
        this.memorizedTarget = null;
        this.reorientTimer = 0f;
        this.stateTimer = STATE_TRANSITION_TIME;
        ((MazeArea) getOwnerArea()).occupyCell(getCurrentMainCellCoordinates(), getCurrentMainCellCoordinates());
    }

    /** Le joueur est-il exactement devant le monstre ? */
    private boolean isJustInFrontOf(ICMazePlayer player) {
        DiscreteCoordinates myCell = getCurrentMainCellCoordinates();
        DiscreteCoordinates frontCell = myCell.jump(getOrientation().toVector());
        return frontCell.equals(player.getCurrentMainCellCoordinates());
    }

    // ---------------- GESTION DES ÉTATS ----------------

    private void handleSleepingState() {
        // 1. tourne à gauche tous les REORIENT_TIME
        if (reorientTimer <= 0f) {
            orientate(leftOf(getOrientation()));
            reorientTimer = REORIENT_TIME;
        }

        // 2. se réveille occasionnellement
        if (stateTimer <= 0f) {
            double pWake = 0.3; // probabilité fixe (tu peux la raffiner avec la difficulté si tu veux)
            if (Math.random() < pWake) {
                state = State.WANDERING;
            }
            stateTimer = STATE_TRANSITION_TIME;
        }
    }

    private boolean checkAccessible(Orientation orient){
        return ((MazeArea) getOwnerArea()).isAccessible(getNextPosition(orient));
    }

    private void handleWanderingState() {
        // 1. orientation aléatoire + déplacement d’un pas
        if (reorientTimer <= 0f) {
            Orientation[] values = Orientation.values();
            Orientation chosen;
            do{
                chosen = values[RandomGenerator.rng.nextInt(values.length)];
            }while (!checkAccessible(chosen));
            orientate(chosen);
            move(10); // 10 "frames" comme suggéré
            reorientTimer = REORIENT_TIME;
        }
        // 2. de temps en temps, regarde s’il voit un joueur
        if (stateTimer <= 0f) {
            ICMazePlayer target = findVisiblePlayer();
            if (target != null) {
                memorizedTarget = target;
                state = State.CHASING;
            }
            stateTimer = STATE_TRANSITION_TIME;
        }
    }

    private void handleChasingState() {
        if (memorizedTarget == null) {
            state = State.WANDERING;
            return;
        }
        // 1. suit le chemin le plus court vers la cible
        if (reorientTimer <= 0f) {
            Orientation next = getNextOrientation();
            orientate(next);
            if (!isJustInFrontOf(memorizedTarget) && checkAccessible(next)){
                move(10);
            }
            reorientTimer = REORIENT_TIME;
        }
        // 2. parfois, se rendort
        if (stateTimer <= 0f) {
            double pSleep = 0.1;
            if (Math.random() < pSleep) {
                state = State.SLEEPING;
            }
            stateTimer = STATE_TRANSITION_TIME;
        }
    }

    private Orientation leftOf(Orientation o) {
        int i = (o.ordinal() + 3) % 4; // -1 modulo 4
        return Orientation.values()[i];
    }

    // ---------------- PATHFINDING / VISION ----------------
    private void occupyNextCell(){
        //renders the occupied cell inaccesible on the graph associated to the MazeArea and frees up the currently occupied tile
        DiscreteCoordinates nextPos = getNextPosition(getOrientation());
        ((MazeArea) getOwnerArea()).occupyCell(getCurrentMainCellCoordinates(), nextPos);
    }
    @Override
    public Orientation getNextOrientation() {
        if (memorizedTarget == null) {
            return getOrientation();
        }
        Queue<Orientation> path = ((MazeArea) getOwnerArea()).shortestPath(getCurrentMainCellCoordinates(), memorizedTarget.getCurrentMainCellCoordinates());
        if (path != null && !path.isEmpty()){
            //graphicPath = new Path(this.getPosition(), new LinkedList<Orientation>(path));
            currentPath = path;
        }
        if (path == null || path.isEmpty()) {
            if (currentPath != null){ //continue follownig the current path, if it isn't null
                Orientation polled = currentPath.poll();
                if (polled != null){
                    return polled;
                }
            }
            return getOrientation();
        }
        return path.poll();
    }

    /** Renvoie un joueur visible dans le champ de vision, ou null. */
    private ICMazePlayer findVisiblePlayer() {
        ICMazeArea area = (ICMazeArea) getOwnerArea();
        List<DiscreteCoordinates> fov = super.getFieldOfViewCells();

        if (fov.contains(((ICMazeArea) getOwnerArea()).getPlayer().getCurrentMainCellCoordinates())) {
            return ((ICMazeArea) getOwnerArea()).getPlayer();
        }
        return null;
    }
    // --------------Drawing----------------
    @Override
    public void draw(Canvas canvas) {
        if (health.getHealth() > 0){
            OrientedAnimation animToPlay = null;
            switch (state){
                case SLEEPING -> animToPlay = sleepAnim;
                case CHASING -> animToPlay = targetedMoveAnim;
                case WANDERING -> animToPlay = moveAnim;
            }
            super.draw(canvas);
            handleAnim(animToPlay, canvas);
            if (graphicPath != null){
                graphicPath.draw(canvas);
            }
        } else{
            if (!destroyed){
                poof.draw(canvas);
            }
        }
    }
    // ---------------- UPDATE ----------------
    @Override
    public void update(float deltaTime) {
        if (health.getHealth() > 0){
            handleRecovery(deltaTime);
            super.update(deltaTime);
            reorientTimer -= deltaTime;
            stateTimer -= deltaTime;
            if (graphicPath != null){
                graphicPath.update(deltaTime);
            }
            switch (state) {
                case SLEEPING -> {handleSleepingState(); sleepAnim.update(deltaTime);}
                case WANDERING -> {handleWanderingState(); moveAnim.update(deltaTime);}
                case CHASING  -> { handleChasingState(); targetedMoveAnim.update(deltaTime);}
            }
        } else {
            if (!destroyed){
                poof.update(deltaTime);
                if (poof.isCompleted() && !destroyed){
                    destroyed = true;
                    //clears the rock, when the cloud is gone
                    ((ICMazeArea) getOwnerArea()).removeItem(this);
                }
            }
        }
    }

    // ---------------- INTERACTIONS ----------------
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }
    @Override
    public boolean wantsViewInteraction() {
        return state == State.CHASING && super.wantsViewInteraction();
    }
    @Override
    public boolean wantsCellInteraction() {
        return false;
    }
    @Override
    public boolean isCellInteractable() {
        return false;
    }
    @Override
    public void beAttacked(int damage) {
        damageActor(damage);
    }
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler , isCellInteraction);
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
    }
    //gestionnaire d'interractions
    private class LogMonsterInteractionHandler implements ICMazeInteractionVisitor {
        public void interactWith(ICMazePlayer player, boolean isCellInteraction){
            player.damageActor(DAMAGE_TO_PLAYER);
        };
    }
}