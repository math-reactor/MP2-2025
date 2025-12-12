package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.List;
import java.util.Queue;

public abstract class LogMonster extends PathFinderEnemy {

    private static final int MAX_LIFE = 5;          // PV max
    private static final int PERCEPTION_RADIUS = 5; // rayon de perception
    private static final int DAMAGE_TO_PLAYER = 1;  // dégâts infligés au joueur

    private static final float REORIENT_TIME = 0.75f;
    private static final float STATE_TRANSITION_TIME = 3f;

    public enum State { SLEEPING, WANDERING, CHASING }

    private State state;
    private DiscreteCoordinates memorizedTarget;

    private float reorientTimer;
    private float stateTimer;

    public LogMonster(ICMazeArea area,
                      Orientation orientation,
                      DiscreteCoordinates position,
                      State initialState) {
        super(area, orientation, position, MAX_LIFE, PERCEPTION_RADIUS);
        this.state = initialState;
        this.memorizedTarget = null;
        this.reorientTimer = 0f;
        this.stateTimer = 0f;
    }

    @Override
    public int getMaxLife() {
        return MAX_LIFE;
    }

    // ---------------- UPDATE ----------------
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        reorientTimer -= deltaTime;
        stateTimer -= deltaTime;

        switch (state) {
            case SLEEPING -> handleSleepingState();
            case WANDERING -> handleWanderingState();
            case CHASING   -> handleChasingState();
        }
    }

    protected abstract void setOrientation(Orientation o);

    // ---------------- INTERACTIONS ----------------
    @Override
    public void interactWith(Interactable other, boolean isCellInteraction) {
        // On ne gère que les interactions de cellule
        if (!isCellInteraction) return;
        if (!(other instanceof ICMazePlayer player)) return;

        if (state == State.SLEEPING || isDead()) {
            return;
        }

        if (isJustInFrontOf(player)) {
            // inflige des dégâts au joueur
            player.takeDamage(DAMAGE_TO_PLAYER);
        } else {
            // mémorise la position du joueur et passe en chasse
            memorizedTarget = player.getCurrentMainCellCoordinates();
            state = State.CHASING;
        }
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

    private void handleWanderingState() {
        // 1. orientation aléatoire + déplacement d’un pas
        if (reorientTimer <= 0f) {
            Orientation[] values = Orientation.values();
            Orientation randomOrient =
                    values[(int) (Math.random() * values.length)];
            orientate(randomOrient);
            move(10); // 10 "frames" comme suggéré
            reorientTimer = REORIENT_TIME;
        }

        // 2. de temps en temps, regarde s’il voit un joueur
        if (stateTimer <= 0f) {
            ICMazePlayer target = findVisiblePlayer();
            if (target != null) {
                memorizedTarget = target.getCurrentMainCellCoordinates();
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
            move(10);
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

    @Override
    public void interactWith(Interactable other) {

    }

    @Override
    public Orientation getNextOrientation() {
        if (memorizedTarget == null) {
            return getOrientation();
        }

        ICMazeArea area = (ICMazeArea) getOwnerArea();
        Queue<Orientation> path =
                area.shortestPath(getCurrentMainCellCoordinates(), memorizedTarget);

        if (path == null || path.isEmpty()) {
            return getOrientation();
        }
        return path.poll();
    }

    /** Renvoie un joueur visible dans le champ de vision, ou null. */
    private ICMazePlayer findVisiblePlayer() {
        ICMazeArea area = (ICMazeArea) getOwnerArea();
        List<DiscreteCoordinates> fov = getFieldOfViewCells();

        for (Actor actor : area.runThrough) {
            if (actor instanceof ICMazePlayer player) {
                if (fov.contains(player.getCurrentMainCellCoordinates())) {
                    return player;
                }
            }
        }
        return null;
    }
}