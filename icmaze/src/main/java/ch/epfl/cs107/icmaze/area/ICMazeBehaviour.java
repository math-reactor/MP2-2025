package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.AreaBehavior;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.engine.actor.Animation;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.Collections;
import java.util.List;

/**
 * Class, which represents the behavioural map associated to a given ICMazeArea (enables collisions against trees)
 */
public class ICMazeBehaviour extends AreaBehavior {
    /**
     * Constructor for the ICMazeBehaviour class, which calls its super-class to initialize the behaviour map,
     * and which also initializes all the cells that are present in the ICMazeArea
     * @param window the game Window
     * @param name the ICMazeArea's name, which is associated to its size (String)
     */
    public ICMazeBehaviour(Window window, String name){
        super(window, name);
        for (int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++){
                //initializes the individual cell with its corresponding type in the behaviour map (can be deduced by its colour)
                super.setCell(x, y, new ICMazeCell(x, y, CellType.toType(getRGB(getHeight() -1-y, x))));
            }
        }
    }

    /**
     * enumeration, which provides all available cell types for the game ICMaze
     */
    public enum CellType {
        NONE(0,false), // Should never be used except in the toType method
        GROUND (-16777216, true),
        WALL(-14112955, false),
        HOLE(-65536, true);
        final int type;
        final boolean isWalkable;

        /**
         * constructor for CellType class Instances, which initializes each class instance's type and isWalkable attribute
         * @param isWalkable whether the cell can be walked through (boolean)
         * @param type the ID of the given cell type (int)
         */
        CellType(int type , boolean isWalkable){
            this.type = type;
            this.isWalkable = isWalkable;
        }

        /**
         * method, which gives the associated CellType class instance for a given ID, if it is a valid one
         * @param type the ID of the given cell type (int)
         * @return The CellType associated to the provided ID
         */
        public static CellType toType(int type){
            for (CellType value : values()){
                if (value.type == type){
                    return value;
                }
            }
            return NONE;
        }
    }

    /**
     * Nested Class, which represents the individual cells, which make up an ICMazeArea
     */
    public class ICMazeCell extends Cell {

        private final static int ANIMATION_DURATION = 24;
        private CellType nature; //the CellType associated to this cell
        private Animation grass;

        /**
         * Constructor for the ICMazeCell class, which calls its super-class to initialize the cell's position and initializes its type
         * and which also initializes all the cells that are present in the ICMazeArea
         * @param x the column of the cell (int)
         * @param y the row of the cell (int)
         * @param type the CellType that is associated to this cell
         */
        private ICMazeCell(int x, int y, CellType type){
            super(x, y);
            nature = type;
        }

        public void createGrass(ICMazeArea area){
        }

        /**
         * Method, which tells us, whether an Interactable can leave this cell
         * @param inter The Interactable that wants to leave this cell
         * @return always true, as a cell can always be left, to avoid problems
         */
        public boolean canLeave(Interactable inter){
            return true;
        }


        /**
         * Method, which tells us, whether an Interactable can enter this cell
         * @param inter The Interactable that wants to enter this cell
         * @return true, if the present entities in this cell can be walked upon. False otherwise
         */
        public boolean canEnter(Interactable inter){
            for (Interactable Item : entities){
                return !Item.takeCellSpace();
            }
            return nature.isWalkable;
        }

        /**
         * Method, which tells us, whether the cell can be interacted with through cell interactions
         * @return always true
         */
        public boolean isCellInteractable(){return true;}

        /**
         * Method, which tells us, whether the cell can be interacted with through view interactions
         * @return always false, as a cell can only be interacted with, when the player is standing on it
         */
        public boolean isViewInteractable(){return false;}

        /**
         * Method that I am too afraid to delete. No idea what it does, but it gives me the impression of being sacred
         */
        public void AreaInteractionVisitor(AreaInteractionVisitor vis, boolean b){}
        /**
         * Also another method that I am too afraid to delete. I do not wish to perturb the blessed game engine's divine will.
         */
        public void acceptInteraction(AreaInteractionVisitor v, boolean isCellInteraction) {}
    }
}