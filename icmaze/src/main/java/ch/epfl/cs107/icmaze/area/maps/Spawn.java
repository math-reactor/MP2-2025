package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Boss.BossProjectile;
import ch.epfl.cs107.icmaze.actor.Boss.FireProjectile;
import ch.epfl.cs107.icmaze.actor.Boss.ICMazeBoss;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;

public class Spawn extends ICMazeArea {
    public boolean victory;
    public Spawn(int keyVal){
        super("SmallArea", keyVal);
    }
    public void createArea(){
        registerActor(new Background(this, getAreaSize()));
        //sets up the different sprites
        super.addItem(new Pickaxe(this, Orientation.DOWN, new DiscreteCoordinates(5,4)));
        super.addItem(new Heart(this, new DiscreteCoordinates(4,5)));
        super.addItem(new Key(this, Orientation.DOWN, new DiscreteCoordinates(6,5), getKeyVal()));
        super.addItem(new FireProjectile(this, Orientation.UP, new DiscreteCoordinates(1,1)));
    }
    public String getTitle(){return "ICMaze/Spawn";}
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
    @Override
    public boolean isOff() {
        return !victory;
    }
    @Override
    public boolean isOn() {
        return victory;
    }
}
