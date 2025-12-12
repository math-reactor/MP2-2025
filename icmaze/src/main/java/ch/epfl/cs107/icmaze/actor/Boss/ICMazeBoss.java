package ch.epfl.cs107.icmaze.actor.Boss;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.icmaze.actor.util.Cooldown;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.OrientedAnimation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

public class ICMazeBoss extends ICMazeActor {
    private final static Vector anchor = new Vector(-0.5f, 0);
    private final static Orientation[] orders = {Orientation.DOWN , Orientation.RIGHT , Orientation.UP, Orientation.LEFT};
    private final static int[][] teleportPositions = {{4, 1}, {8, 4}, {4, 8}, {1, 4}};
    private final double SHOOTINTERVAL = 5;
    private final int ANIMATION_DURATION = 60;
    private int MAX_LIFE = 5;
    private OrientedAnimation UI;
    private boolean angered = false;
    private double timeInterval = 0;
    private Animation poof;
    private boolean destroyed = false;

    public ICMazeBoss(Area setArea){
        super(setArea, Orientation.DOWN, new DiscreteCoordinates(setArea.getWidth()/2, setArea.getHeight()/2));
        UI = new OrientedAnimation("icmaze/boss", ANIMATION_DURATION/4, this , anchor , orders , 3, 2, 2, 32, 32, true);
        poof = new Animation("icmaze/vanish", 7, 2, 2, this , 32, 32, new Vector(-0.5f, 0.0f), ANIMATION_DURATION/7, false);
        health = new Health(this, Transform.I.translated(0, 1.25f), MAX_LIFE , false);
        setCd();
    }

    private void teleportRandom(){
        int Chosen;
        Orientation randomOr;
        do{
            Chosen = RandomGenerator.rng.nextInt(4);
            randomOr = orders[Chosen].opposite();
        } while(getOrientation() == randomOr);
        int[] chosenPos = teleportPositions[Chosen];
        Area currentArea = getOwnerArea();
        exitArea();
        orientate(randomOr);
        enterArea(currentArea, new DiscreteCoordinates(chosenPos[0], chosenPos[1]));
    }

    @Override
    public void exitArea() {
        getOwnerArea().leaveAreaCells(this, getCurrentCells());
        getOwnerArea().unregisterActor(this);
    }

    public void beAttacked(int damage){
        if (angered && !getRecovery()){
            health.decrease(damage);
            drawFrame = 0;
            setRecovery(true);
            if (health.getHealth() > 0){
                teleportRandom();
            }
        }
        angered = true;
    }

    public boolean takeCellSpace(){
        return true;
    }
    @Override
    public boolean isViewInteractable() {
        return true;
    }

    private void attack(){
        int[] commonPos = {-1,-1};
        switch (getOrientation()){
            case DOWN -> commonPos = new int[]{-1, 7};
            case UP -> commonPos = new int[]{-1, 2};
            case RIGHT -> commonPos = new int[]{2, -1};
            case LEFT -> commonPos = new int[]{7, -1};
        }
        int randomHole = RandomGenerator.rng.nextInt(1, 9);
        for (int i = 1; i < 9; i++){
            int[] newCoords = commonPos.clone();
            if (i != randomHole){
                if (commonPos[0] < 0){
                    newCoords[0] = i;
                }
                if (commonPos[1] < 0){
                    newCoords[1] = i;
                }
                BossProjectile newProj = new FireProjectile(getOwnerArea(), getOrientation(), new DiscreteCoordinates(newCoords[0], newCoords[1]));
                ((ICMazeArea) getOwnerArea()).addItem(newProj);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        if (health.getHealth() > 0){
            if (angered){
                handleRecovery(deltaTime);
                timeInterval += deltaTime;
                if (timeInterval >= SHOOTINTERVAL){
                    timeInterval = 0;
                    attack();
                }
            }
            UI.update(deltaTime);
        }
        else {
            if (!destroyed){
                poof.update(deltaTime);
                if (poof.isCompleted() && !destroyed){
                    destroyed = true;
                    //clears the rock, when the cloud is gone
                    ((BossArea) getOwnerArea()).killBoss();
                }
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if (health.getHealth() > 0){
            handleAnim(UI, canvas);
        }
        else{
            if (!destroyed){
                poof.draw(canvas);
            }
        }
    }
    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }
}
