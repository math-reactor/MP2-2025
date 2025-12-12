package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.MazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.net.CookieHandler;
import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {
    private final int ANIMATION_DURATION = 24;
    private final static int MAX_LIFE = 3;
    private Sprite UI;
    private Animation poof;
    private boolean destroyed = false;
    private boolean recovering = false;
    private Health health = new Health(this, Transform.I.translated(0, 0.25f), MAX_LIFE , false);
    private Cooldown cd = new Cooldown(health.STANDARD_COOLDOWN_TIME);
    private int drawFrame;

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
    public void damage(int damage){
        if (!recovering){
            health.decrease(damage);
            drawFrame = 0;
            recovering = true;
        }
    }
    private void handleRockDestruction(){
        //destroys the rock, and has a probability to give a heart
        destroyed = true;
        DiscreteCoordinates coords = getCurrentMainCellCoordinates();
        ((MazeArea) getOwnerArea()).makePointWalkable(coords);
        ((ICMazeArea) getOwnerArea()).removeItem(this);
        int randVal = RandomGenerator.rng.nextInt(3);
        if (randVal == 0){
            ((ICMazeArea) getOwnerArea()).replaceWallByHeart(coords);
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (health.getHealth() > 0){
            if (recovering){
                if (drawFrame % 4 == 0){
                    UI.draw(canvas);
                }
                drawFrame += 1;
                health.draw(canvas);
            }
            else {
                UI.draw(canvas);
            }
        }
        else{
            //draws the destruction cloud
            if (!destroyed){
                poof.draw(canvas);
            }
            if (poof.isCompleted() && !destroyed){
                //clears the rock, when the cloud is gone
                handleRockDestruction();
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        if (recovering){
            recovering = !cd.ready(deltaTime);
        }
        else {
            cd.reset();
        }
        if (health.getHealth() <= 0 && !poof.isCompleted()){
            poof.update(deltaTime);
        }
        super.update(deltaTime);
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    @Override
    public boolean takeCellSpace() {
        return !destroyed;
    }
    @Override
    public boolean isCellInteractable() {
        return health.getHealth() > 0;
    }
    @Override
    public boolean isViewInteractable() {
        return health.getHealth() > 0;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
