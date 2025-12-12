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

public abstract class Enemy extends ICMazeActor
        implements Interactor, Interactable {

    private int currentLife;

    private boolean dead;

    protected Enemy(ICMazeArea area,
                    Orientation orientation,
                    DiscreteCoordinates position,
                    int initialLife) {
        super(area, orientation, position);
        this.currentLife = initialLife;
        this.dead = false;
    }


    public abstract int getMaxLife();

    public final boolean isDead() {
        return dead;
    }

    public final int getCurrentLife() {
        return currentLife;
    }

    public void inflictDamage(int amount) {
        if (dead || amount <= 0) return;

        currentLife -= amount;
        if (currentLife <= 0) {
            currentLife = 0;
            die();
        }
    }

    public void heal(int amount) {
        if (dead || amount <= 0) return;

        currentLife = Math.min(currentLife + amount, getMaxLife());
    }

    protected void die() {
        if (dead) return;
        dead = true;

        // TODO: à TOI de :
        //  1. lancer l’animation de mort (voir annexe 7.3.5)
        //  2. à la fin de l’animation, unregisterActor(this)
        //     (tu peux soit faire ça ici, soit dans update())
    }

    @Override
    public boolean takeCellSpace() {
        return !dead;
    }

    @Override
    public boolean isCellInteractable() {
        return !dead;
    }

    @Override
    public boolean isViewInteractable() {
        return !dead;
    }

    @Override
    public boolean wantsCellInteraction() {
        return !dead;
    }

    @Override
    public boolean wantsViewInteraction() {
        return !dead;
    }

}