package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {
    private final int ANIMATION_DURATION = 24;
    private Sprite UI;
    private int Health = 2;
    private Animation poof;
    private boolean canAttack;
    private boolean destroyed = false;
    public Rock(Area area, DiscreteCoordinates pos){
        super(area, Orientation.DOWN, pos);
        UI = new Sprite("rock.2", 1f, 1f, this);
        enterArea(area, pos);
        poof = new Animation("icmaze/vanish", 7, 2, 2, this , 32, 32, new Vector(-0.5f, 0.0f), ANIMATION_DURATION/7, false);
    }
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setCurrentPosition(position.toVector());
    }
    public void damage(){
        canAttack = false;
        Health -= 1;
    }
    public boolean canBeAttacked() {
        return canAttack;
    }

    @Override
    public void draw(Canvas canvas) {
        if (Health > 0){
            UI.draw(canvas);
        }
        else{
            if (!destroyed){
                poof.draw(canvas);
            }
            if (poof.isCompleted() && !destroyed){
                destroyed = true;
                DiscreteCoordinates coords = getCurrentMainCellCoordinates();
                getOwnerArea().purgeAreaCellsFrom(this);
                ((ICMazeArea) getOwnerArea()).removeItem(this);
                int randVal = RandomGenerator.rng.nextInt(3);
                if (randVal == 0){
                    ((ICMazeArea) getOwnerArea()).replaceWallByHeart(coords);
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (Health <= 0 && !poof.isCompleted()){
            poof.update(deltaTime);
        }
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    @Override
    public boolean takeCellSpace() {
        return Health > 0;
    }
    @Override
    public boolean isCellInteractable() {
        return Health > 0;
    }
    @Override
    public boolean isViewInteractable() {
        return Health > 0;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
        canAttack = true;
    }
}
