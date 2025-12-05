package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.Collections;
import java.util.List;

public class Rock extends AreaEntity {
    private Sprite UI;
    public Rock(Area area, DiscreteCoordinates pos){
        super(area, Orientation.DOWN, pos);
        UI = new Sprite("rock.2", 1f, 1f, this);
        enterArea(area, pos);
    }
    public void enterArea(Area area, DiscreteCoordinates position){
        area.registerActor(this);
        setCurrentPosition(position.toVector());
    }
    @Override
    public void draw(Canvas canvas) {
        UI.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }
    @Override
    public boolean takeCellSpace() {
        return true;
    }
    @Override
    public boolean isCellInteractable() {
        return true;
    }
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {}
}
