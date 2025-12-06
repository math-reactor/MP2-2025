package ch.epfl.cs107.icmaze.actor.collectable;

import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class Heart extends Consumable  {
    private Animation UIelement;
    public Heart(Area area, DiscreteCoordinates coords, final int ANIMATION_DURATION){
        super(area, coords, ANIMATION_DURATION);
        setUI(new Animation("icmaze/heart", 4, 1, 1, this , 16, 16, ANIMATION_DURATION/4, true));
    }
    @Override
    public void draw(Canvas canvas) {
        if (!isCollected())
        super.draw(canvas);
    }
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this , isCellInteraction);
        collect();
    }
}
