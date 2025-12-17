package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

/**
 * Class, which represents the behavioral map associated with an associated ICMazeArea
 */
public class ICMazeBehaviour extends AreaBehavior {
    public ICMazeBehaviour(Window window, String name){
        super(window, name);
        for (int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++){
                super.setCell(x, y, new ICMazeCell(x, y, CellType.toType(getRGB(getHeight() -1-y, x))));
            }
        }
    }
    public enum CellType {
        NONE(0,false), // Should never been used except inthe toType method
        GROUND (-16777216, true),
        WALL(-14112955, false),
        HOLE(-65536, true);
        final int type;
        final boolean isWalkable;
        CellType(int type , boolean isWalkable){
            this.type = type;
            this.isWalkable = isWalkable;
        }
        public static CellType toType(int type){
            for (CellType value : values()){
                if (value.type == type){
                    return value;
                }
            }
            return NONE;
        }
    }
    public class ICMazeCell extends Cell {
        private CellType nature;
        private ICMazeCell(int x, int y, CellType type){
            super(x, y);
            nature = type;
        }

        public boolean canLeave(Interactable inter){
            return true;
        }
        public boolean canEnter(Interactable inter){
            for (Interactable Item : entities){
                return !Item.takeCellSpace();
            }
            return nature.isWalkable;
        }
        public boolean isCellInteractable(){
            return true;
        }
        public boolean isViewInteractable(){
            return false;
        }
        public void AreaInteractionVisitor(AreaInteractionVisitor vis, boolean b){}
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {}
    }
}