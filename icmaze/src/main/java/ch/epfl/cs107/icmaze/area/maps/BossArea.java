package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Boss.ICMazeBoss;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.Signal;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;


public class BossArea extends ICMazeArea{
    private ICMazeBoss boss;
    public BossArea(){
        super("SmallArea", -1);
    }
    public void createArea(){
        registerActor(new Background(this, getAreaSize()));
        boss = new ICMazeBoss(this);
        super.addItem(boss);
    }
    public void killBoss(){
        DiscreteCoordinates bossPos = boss.getCurrentMainCellCoordinates();
        removeItem(boss);
        Key newKey = new Key(this, Orientation.DOWN, bossPos, -1);
        addItem(newKey);
    }
    public String getTitle(){return "ICMaze/Boss";}
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

}
