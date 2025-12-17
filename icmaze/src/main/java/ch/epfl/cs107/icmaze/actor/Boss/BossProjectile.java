package ch.epfl.cs107.icmaze.actor.Boss;

import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

/**
 * Abstract vlass, which represents the projectile launched by the boss, with all constants
 */
public abstract class BossProjectile extends ICMazeActor {
    //constants
    protected final static int MAXDISTANCE = 7;
    protected final static int SPEED = 1;
    protected final int MOVE_DURATION = 30;
    protected final int ANIMATION_DURATION = 12;
    protected final int DAMAGE = 1;

    /**
     * Constructor for the BossProjectile class (used by the FireProjectile), which calls its super-constructor
     */
    public BossProjectile(Area setArea, Orientation orient, DiscreteCoordinates setPos){
        super(setArea, orient, setPos);
    }
}
