package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

import java.util.ArrayList;
import java.util.List;

public abstract class PathFinderEnemy extends Enemy {


    private final int perceptionRadius;

    protected PathFinderEnemy(ICMazeArea area,
                              Orientation orientation,
                              DiscreteCoordinates position,
                              int initialLife,
                              int perceptionRadius) {
        super(area, orientation, position, initialLife);
        this.perceptionRadius = perceptionRadius;
    }


    // ------------ INTERACTIONS ------------
    public abstract void interactWith(Interactable other);

    public abstract Orientation getNextOrientation();

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        List<DiscreteCoordinates> cells = new ArrayList<>();

        DiscreteCoordinates center = getCurrentMainCellCoordinates();
        int cx = center.x;
        int cy = center.y;

        for (int dx = -perceptionRadius; dx <= perceptionRadius; ++dx) {
            for (int dy = -perceptionRadius; dy <= perceptionRadius; ++dy) {
                cells.add(new DiscreteCoordinates(cx + dx, cy + dy));
            }
        }
        return cells;
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