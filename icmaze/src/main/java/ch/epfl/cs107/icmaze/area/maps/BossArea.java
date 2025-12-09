package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.engine.actor.Foreground;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;


public class BossArea extends ICMazeArea {

    public BossArea(){
        super("SmallArea", -1);
    }
    public void createArea(){
        registerActor(new Background(this, name));
    }
    public String getTitle(){return "ICMaze/Boss";}
    public String getSize(){return "SmallArea";}
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
