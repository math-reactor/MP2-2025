package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import javax.swing.text.html.HTMLDocument;

public abstract class Equipment extends Collectibles{
    private Sprite UI;
    public Equipment(Area area, Orientation orient, DiscreteCoordinates coords){
        super(area, orient, coords);
    }
    public void setUI(Sprite setSprite){
        UI = setSprite;
    }
    public void draw(Canvas canvas){
        UI.draw(canvas);
    }
}
