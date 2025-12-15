package ch.epfl.cs107.icmaze.actor;   // adapte le package

// imports à adapter selon ton projet
// import ch.epfl.cs107.play.window.Canvas;
// import ch.epfl.cs107.play.math.DiscreteCoordinates;
// import ch.epfl.cs107.play.math.RegionOfInterest;
// import ch.epfl.cs107.play.game.areagame.actor.*;
// import ch.epfl.cs107.play.game.areagame.Area;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public abstract class Enemy extends ICMazeActor implements Interactor, Interactable {

    protected Enemy(ICMazeArea area, Orientation orientation, DiscreteCoordinates position, int initialLife) {
        super(area, orientation, position);
    }

    public final boolean isDead() {
        return health.getHealth() <= 0;
    }

    @Override
    public boolean takeCellSpace() {
        return !isDead();
    }
    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return !isDead();
    }

    @Override
    public boolean wantsCellInteraction() {
        return false;
    }

    @Override
    public boolean wantsViewInteraction() {
        return !isDead();
    }

}