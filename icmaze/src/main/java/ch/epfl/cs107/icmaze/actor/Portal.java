package ch.epfl.cs107.icmaze.actor;

import ch.epfl.cs107.icmaze.Size;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.handler.ICMazeInteractionVisitor;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

import java.util.List;

public class Portal extends AreaEntity{
    private String parentArea;
    private PortalState state;
    private String destinationArea;
    private DiscreteCoordinates destinationCoordinates;
    private int keyId;
    private final Sprite[] sprites;

    public static final int NO_KEY_ID = Integer.MIN_VALUE;

    public Portal(Area area,
                  Orientation orientation,
                  DiscreteCoordinates mainCell,
                  String destinationArea,
                  DiscreteCoordinates destinationCoordinates,
                  int keyId) {

        super(area, orientation, mainCell);
        parentArea = area.getTitle();
        this.state = PortalState.INVISIBLE;
        this.destinationArea = destinationArea;
        this.destinationCoordinates = destinationCoordinates;
        this.keyId = keyId;

        Sprite invisible = new Sprite(
                "icmaze/invisibleDoor_" + orientation.ordinal(),
                (orientation.ordinal() + 1) % 2 + 1,
                orientation.ordinal() % 2 + 1,
                this
        );

        Sprite locked = new Sprite(
                "icmaze/chained_wood_" + orientation.ordinal(),
                (orientation.ordinal() + 1) % 2 + 1,
                orientation.ordinal() % 2 + 1,
                this
        );

        sprites = new Sprite[]{invisible, locked};
    }
    public String getParentArea(){return parentArea;}
    // --- getters ---
    public String getDestinationArea() {
        return destinationArea;
    }

    public DiscreteCoordinates getDestinationCoordinates() {
        return destinationCoordinates;
    }

    public int getKeyId() {
        return keyId;
    }

    public PortalState getState() {
        return state;
    }

    // --- setters ---
    public void setState(PortalState newState) {
        this.state = newState;
    }

    public void setDestinationArea(String destinationArea) {
        this.destinationArea = destinationArea;
    }

    public void setDestinationCoordinates(ICMazeArea.AreaPortals direction, String nextAreaSize) {
        DiscreteCoordinates arrival;
        int size = Size.getSize(nextAreaSize);
        switch (direction) {
            case N -> arrival = new DiscreteCoordinates(size / 2 + 1, 1);
            case S -> arrival = new DiscreteCoordinates(size / 2 + 1, size );
            case W -> arrival = new DiscreteCoordinates(size, size / 2+1);
            case E -> arrival = new DiscreteCoordinates(1, size / 2+1);
            default -> throw new IllegalStateException();
        }
        destinationCoordinates = arrival;
    }

    public void setKeyId(int keyId) {
        this.keyId = keyId;
    }

    // --- AreaEntity overrides ---
    @Override
    public List<DiscreteCoordinates> getCurrentCells() {
        DiscreteCoordinates c = getCurrentMainCellCoordinates();
        if (getOrientation() == Orientation.DOWN || getOrientation() == Orientation.UP){
            return List.of(c, new DiscreteCoordinates(c.x + 1, c.y));
        }
        return List.of(c, new DiscreteCoordinates(c.x, c.y+1));
    }

    @Override
    public boolean takeCellSpace() {
        return state != PortalState.OPEN;
    }

    @Override
    public boolean isCellInteractable() {
        // même invisible ou verrouillé, le joueur doit pouvoir être détecté dessus
        return true;
    }

    @Override
    public boolean isViewInteractable() {
        return state == PortalState.LOCKED;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {
        ((ICMazeInteractionVisitor) v).interactWith(this, isCellInteraction);
    }

    @Override
    public void draw(Canvas canvas) {
        if (state != PortalState.OPEN){
            Sprite s = switch (state) {
                case INVISIBLE -> sprites[0];
                case LOCKED -> sprites[1];
                case OPEN -> sprites[2];
            };
            s.draw(canvas);

        }
    }
}