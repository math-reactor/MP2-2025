package ch.epfl.cs107.icmaze.actor.Boss;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

/**
 * Class, which represents the FireProjectiles launched by the boss, which move in a straight line and damage the player
 */
public class FireProjectile extends BossProjectile implements Interactor {
    //variables
    private Animation anim;
    private DiscreteCoordinates currentPos;
    private BossProjInteractionHandler interactionHandler;
    private int steps = 1;

    /**
     * Constructor for the FireProjectile class, initializes its UI elements and makes it enter the provided area
     * @param setArea the area in which the FireProjectile will be spawned
     * @param orient the default orientation of the individual FireProjectile
     * @param setPos the default position at which the FireProjectile will be spawned at
     */
    public FireProjectile(Area setArea, Orientation orient, DiscreteCoordinates setPos){
        super(setArea, orient, setPos);
        currentPos = setPos;
        anim = new Animation("icmaze/magicFireProjectile" , 4, 1, 1, this , 32, 32, ANIMATION_DURATION/4, true);
        enterArea(getOwnerArea(), getCurrentMainCellCoordinates());
        interactionHandler = new BossProjInteractionHandler();
    }

    /**
     * method, which redraws the FireProjectile on the provided canvas
     * @param canvas the canvas on which the projectile will be drawn on
     */
    @Override
    public void draw(Canvas canvas){
        if (steps < MAXDISTANCE){
            //if the projectile's traveled distance is within bounds, its Animation will be updated
            if (!getCurrentMainCellCoordinates().equals(currentPos)){
                steps += 1;
                currentPos = getCurrentMainCellCoordinates();
            }
            move(MOVE_DURATION/SPEED);
            anim.draw(canvas);
        }
        else{
            //Otherwise, the projectile will be fully removed from the game
            ((ICMazeArea) getOwnerArea()).removeItem(this);
            getOwnerArea().purgeAreaCellsFrom(this);
        }
    }

    /**
     * default redefinition of beAttacked for FireProjectile (does absolutely nothing to the Projectile)
     */
    @Override
    public void beAttacked(int damage) {}

    /**
     * method, which updates the FireProjectile with a given time interval, whilst it is still within bounds
     * @param deltaTime A double - the time interval
     */
    @Override
    public void update(float deltaTime) {
        if (steps < MAXDISTANCE){ //updates only if the projectile is within bounds
            anim.update(deltaTime);
            super.update(deltaTime);
        }
    }

    /**
     * method, which returns whether the FireProjectile can be interacted with through cell interactions
     * @return false, as the FireProjectile itself starts the interaction (It is the dominant force in the interaction)
     */
    @Override
    public boolean isCellInteractable() {return false;}

    /**
     * method, which returns whether the FireProjectile can be interacted with through view interactions
     * @return false, as the FireProjectile itself starts the interaction (It is the dominant force in the interaction)
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * method, which returns whether the FireProjectile wants to proceed with cell interactions
     * @return true, as long as the FireProjectile is within bounds (attack against player)
     */
    @Override
    public boolean wantsCellInteraction() {
        return steps < MAXDISTANCE;
    }

    /**
     * method, which returns whether the FireProjectile wants to proceed with view interactions
     * @return true, as long as the FireProjectile is within bounds (attack against boss, which inflicts 0 damage)
     */
    @Override
    public boolean wantsViewInteraction() {
        return steps < MAXDISTANCE;
    }

    /**
     * method, which returns the cell right in front of the FireProjectile
     * @return A List<DiscreteCoordinates>, containing only the coordinates of the cell in front of the FireProjector
     */
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }

    /**
     * method, which renders the FireBall harmless, after it hits a player
     */
    private void hit(){
        steps = MAXDISTANCE+1;
    }

    /**
     * method, which asks another actor, if it can proceed with an interaction with that other actor
     * @param other the other Interactable, with which the FireBall wants to interact with
     * @param isCellInteraction A boolean, which gives whether this is a cell interaction or not
     */
    @Override
    public void interactWith(Interactable other , boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler , isCellInteraction);
    }

    /**
     * redefinition of ICMazeActor's acceptInteraction, does nothing by default
     * @param v the other Interactor, which wants to interact with the Player
     * @param isCellInteraction whether this is a cell interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction){}

    /**
     * This class is the fireball's interaction handling class,
     * which handles interactions between the player and specific actors
     */
    private class BossProjInteractionHandler implements ICMazeInteractionVisitor {
        /**
         * method which handles interactions between the FireBall and the Player actor (Player is damaged)
         * @param player the Player actor that has been hit
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(ICMazePlayer player, boolean isCellInteraction){
            if (isCellInteraction && steps < MAXDISTANCE){
                hit();
                player.beAttacked(DAMAGE);
            }
        };

        /**
         * method which handles interactions between the FireBall and the ICMazeBoss actor (no damage, fireball simply disappears)
         * @param boss the ICMazeBoss actor that has been hit
         * @param isCellInteraction whether this is a cell interaction or not
         */
        public void interactWith(ICMazeBoss boss, boolean isCellInteraction){
            if (!isCellInteraction){
                hit();
            }
        };
    }
}
