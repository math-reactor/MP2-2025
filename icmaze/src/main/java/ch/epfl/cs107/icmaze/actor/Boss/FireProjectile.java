package ch.epfl.cs107.icmaze.actor.Boss;

import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.actor.Interactor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class FireProjectile extends BossProjectile implements Interactor {
    private Animation anim;
    private DiscreteCoordinates currentPos;
    private BossProjInteractionHandler interactionHandler;

    public FireProjectile(Area setArea, Orientation orient, DiscreteCoordinates setPos){
        super(setArea, orient, setPos);
        currentPos = setPos;
        anim = new Animation("icmaze/magicFireProjectile" , 4, 1, 1, this , 32, 32, ANIMATION_DURATION/4, true);
        enterArea(getOwnerArea(), getCurrentMainCellCoordinates());
        interactionHandler = new BossProjInteractionHandler();
    }
    public void draw(Canvas canvas){
        if (steps < MAXDISTANCE){ //if the projectile has moved into a new cell
            if (!getCurrentMainCellCoordinates().equals(currentPos)){
                steps += 1;
                currentPos = getCurrentMainCellCoordinates();
            }
            move(MOVE_DURATION/SPEED);
            anim.draw(canvas);
        }
        else{
            ((ICMazeArea) getOwnerArea()).removeItem(this);
            getOwnerArea().purgeAreaCellsFrom(this);
        }
    }

    @Override
    public void update(float deltaTime) {
        anim.update(deltaTime);
        super.update(deltaTime);
    }

    @Override
    public boolean isCellInteractable() {
        return false;
    }

    @Override
    public boolean isViewInteractable() {
        return false;
    }
    @Override
    public boolean wantsCellInteraction() {
        return steps <= MAXDISTANCE;
    }
    @Override
    public boolean wantsViewInteraction() {
        return false;
    }
    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates().jump(getOrientation().toVector()));
    }
    private void hit(ICMazePlayer player){
        steps = MAXDISTANCE+1;
        ((ICMazeArea) getOwnerArea()).removeItem(this);
    }

    @Override
    public void interactWith(Interactable other , boolean isCellInteraction) {
        other.acceptInteraction(interactionHandler , isCellInteraction);
    }
    private class BossProjInteractionHandler implements ICMazeInteractionVisitor {
        //gère les interactions entre le projectile et le joueur
        public void interactWith(ICMazePlayer player, boolean isCellInteraction){
            if (isCellInteraction){
                hit(player);
            }
        };
    }
}
