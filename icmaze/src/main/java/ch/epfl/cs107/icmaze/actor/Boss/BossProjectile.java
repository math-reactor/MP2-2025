package ch.epfl.cs107.icmaze.actor.Boss;

import ch.epfl.cs107.icmaze.actor.*;
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

import java.util.Collections;
import java.util.List;

public abstract class BossProjectile extends ICMazeActor {
    protected final static int MAXDISTANCE = 7;
    protected final static int SPEED = 1;
    protected final int MOVE_DURATION = 30;
    protected final int ANIMATION_DURATION = 12;
    protected int steps = 1;
    public BossProjectile(Area setArea, Orientation orient, DiscreteCoordinates setPos){
        super(setArea, orient, setPos);
    }
}
