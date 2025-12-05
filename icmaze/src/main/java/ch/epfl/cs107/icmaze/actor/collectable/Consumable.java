package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public abstract class Consumable extends Collectibles{
    private Animation UIelement;
    private final int animDur;
    public Consumable(Area area, DiscreteCoordinates coords, int newAnimDur){
        super(area, coords);
        animDur = newAnimDur;
    }

    public void setUI(Animation newUI) {
        UIelement = newUI;
    }

    public void draw(Canvas canvas) {
        UIelement.update(animDur);
        UIelement.draw(canvas);
    }
}
