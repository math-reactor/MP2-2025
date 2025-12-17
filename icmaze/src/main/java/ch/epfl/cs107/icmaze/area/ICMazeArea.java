package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
import ch.epfl.cs107.icmaze.handler.Damageable;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.actor.AreaEntity;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.signal.Signal;
import ch.epfl.cs107.play.signal.logic.Logic;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ICMazeArea extends Area  implements Logic {
    private final Portal[] portals = new Portal[AreaPortals.values().length];
    private ArrayList<Actor> runThrough; //Contains all entities in this area, for easy access
    public final static float DEFAULT_SCALE_FACTOR = 11.f;
    public final static float DYNAMIC_SCALE_MULTIPLIER = 1.375f;
    public final static float MAXIMUM_SCALE = 30.f;
    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    private Window window;
    private String name;
    private int size;
    private boolean victory;

    //portal information
    private String nextArea;
    private String nextAreaSize;
    private AreaPortals nextDir;
    private String prevArea;
    private String prevAreaSize;
    private AreaPortals prevDir;
    private int keyVal;

    public ICMazeArea(String setName, int setKeyVal){
        runThrough = new ArrayList<>();
        name = setName;
        keyVal = setKeyVal;
    }

    /**
     * Area specific callback to initialise the instance
     */
    public abstract void createArea();
    /**
     * Callback to initialise the instance of the area
     * @param window (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return true if the area is instantiated correctly, false otherwise
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        this.window = window;
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            setBehavior(new ICMazeBehaviour(window, name));
            size = getWidth();
            initPortals();
            createArea();
            return true;
        }
        return false;
    }
    /**
    *enum, which gives us all four possible orientations of the AreaPortals in an area
     */
    public enum AreaPortals {
        N(Orientation.UP),
        W(Orientation.LEFT),
        S(Orientation.DOWN),
        E(Orientation.RIGHT);

        private final Orientation orientation;

        AreaPortals(Orientation orientation) {
            this.orientation = orientation;
        }
        public Orientation getOrientation() {
            return orientation;
        }
    }

    public Portal getPortal(AreaPortals direction){
        //getter for the portal in the given AreaPortals direction (@param direction)
        return portals[direction.ordinal()];
    }
    /**
        * Setter of the teleportation data of the area's entry portal
        * @param setPrevAreaSize (String): the size of the previous area (SmallArea - 8, MediumArea - 16, LargeArea - 32)
        * @param setPrevArea (String): the previous area's name, which will identify where to teleport the player
        * @param setPrevDir (AreaPortals): the orientation of the previous area's portal (which allowed the player to enter this area) in the previous area
        * @return void
     */
    public void setPreviousPortal(String setPrevAreaSize, String setPrevArea, AreaPortals setPrevDir){
        //sets the open (exit) portal of the current area
        prevAreaSize = setPrevAreaSize;
        prevArea = setPrevArea;
        prevDir = setPrevDir;
    }
    /**
     * Setter of the teleportation data of the area's exit portal
     * @param setNextAreaSize (String): the size of the previous area (SmallArea - 8, MediumArea - 16, LargeArea - 32)
     * @param setNextArea (String): the previous area's name, which will identify where to teleport the player
     * @param setNextDir (AreaPortals): the orientation of the next area's portal (to thich the player will teleport to, if he moves forward)
     * @return void
     */
    public void setNextPortal(String setNextAreaSize, String setNextArea, AreaPortals setNextDir){
        //sets the closed (next) portal of the current area
        nextAreaSize = setNextAreaSize;
        nextArea = setNextArea;
        nextDir = setNextDir;
    }
    /**
     * setup of the Area's four default portals. If they don't have their data set by setPreviousPortal or setNextPortal,
     * they are considered as in their default state and will therefore be blocked off, using tree sprites and an invisible wall.
     * @return void
     */
    private void initPortals() {
        //initlailzes all four portals in the current area's four directions
        for (AreaPortals ap : AreaPortals.values()) {
            DiscreteCoordinates mainCell;
            //gives the main coordinate for the graphic placement of the portal
            switch (ap) {
                case N -> mainCell = new DiscreteCoordinates(size / 2 - 1, size-1);
                case S -> mainCell = new DiscreteCoordinates(size / 2 - 1, 0);
                case W -> mainCell = new DiscreteCoordinates(0, size / 2 - 1);
                case E -> mainCell = new DiscreteCoordinates(size-1, size / 2-1);
                default -> throw new IllegalStateException();
            }

            //default values for the setup of the portal
            Orientation spriteOrientation = ap.getOrientation();
            String destAreaName = null;
            DiscreteCoordinates defaultSpawn = new DiscreteCoordinates(1,1);
            Portal portal = new Portal(this, spriteOrientation, mainCell, destAreaName, defaultSpawn, keyVal);

            //creates an open or locked door, based on the area's attributes
            if (ap == nextDir || ap == prevDir){
                if (ap == prevDir){
                    portal.setState(PortalState.OPEN);
                    portal.setDestinationCoordinates(prevDir, prevAreaSize);
                    portal.setDestinationArea(prevArea);
                }
                else {
                    portal.setState(PortalState.LOCKED);
                    portal.setDestinationCoordinates(nextDir, nextAreaSize);
                    portal.setDestinationArea(nextArea);
                }
            }

            portals[ap.ordinal()] = portal;
            addItem(portal);
        }
    }
    /**
     * Adds a given item to the list of Actors, which are present in the current area. This enables for easier access
     * @param Item the item to add to the list
     * @param isPlayer whether the added item is a player. This decides whether the function will register the player
     * @return void
     */
    public void addItem(Actor Item, boolean isPlayer){
        //adds the created items into the area
        runThrough.add((Actor) Item);
        if (!isPlayer){
            this.registerActor(Item);
        }
    }

    /**
     * default version of the addItem(Actor Item, boolean isPlayer) method. It is used for everything that isn't a player
     * @param Item the item to add to the list
     * @return void
     */
    public void addItem(Actor Item){
        addItem(Item, false);
    }

    /**
     * updates the items that are present in the current area's list, by registering them all,
     * and by making them all enter the current area. This method is to be used when areas are switched
     * @return void
     */
    public void renewList(){
        //registers all the actors of the current area
        for (Actor actor : runThrough){
            this.registerActor(actor);
            enterAreaCells((Interactable) actor, ((Interactable) actor).getCurrentCells());
        }
        if (victory){
            killAll();
        }
    }

    /**
     * completely removes a given item from the current area's list of present actors
     * @param Item the item to completely remove (if it isn't a pleyer)
     * @param isPlayer whether the item is a player. If true, we suppose that the game will handle player transit
     * @return void
     */
    public void removeItem(Actor Item, boolean isPlayer){
        //removes the given item from the list
        for (int i = 0; i < runThrough.size(); i++){
            if (runThrough.get(i).equals((Actor) Item)){
                if (!isPlayer){
                    this.unregisterActor(Item);
                    purgeAreaCellsFrom((Interactable) runThrough.get(i));
                }
                runThrough.remove(i);
                break;
            }
        }
    }

    /**
     * default version of the removeItem(Actor Item, boolean isPlayer), specifically for non-player actors
     * @param Item the item to completely remove (if it isn't a pleyer)
     * @return void
     */
    public void removeItem(Actor Item){
        removeItem(Item, false);
    }

    /**
     * clears the current area's list of actors (not completely). This is used to remove all traces of the current area during area transits
     * @return void
     */
    public void clearList(){
        //removes all actors in the current area
        int maxSize = runThrough.size();
        for (int i = 0; i < maxSize; i++){
            this.unregisterActor(runThrough.get(i));
            purgeAreaCellsFrom((Interactable) runThrough.get(i));
        }
    }
    /**
     * Kills all the actors of the Damageable type, which are present in the current area
     * @return void
     */
    protected void killAll(){
        for (Actor actor : runThrough){
            if (actor instanceof Damageable){
                ((Damageable) actor).beAttacked(Integer.MAX_VALUE);
            }
        }
    }
    /**
     * Method to obtain the current area's player actor
     * @return ICMazePlayer or null
     */
    public ICMazePlayer getPlayer(){
        for (Actor actor : runThrough){
            if (actor.getClass() == ICMazePlayer.class){
                return (ICMazePlayer) actor;
            }
        }
        return null;
    }

    /**
     * Getter to obtain the current area's name, which is associated to its size
     * @return String name
     */
    public String getAreaSize(){return name;}

    /**
     * Getter to obtain the current area's key value
     * @return int key value
     */
    protected int getKeyVal(){return keyVal;}

    /**
     * Setter to obtain the set the current area's victory status to true. It then launches whatever event happens after the victory
     * @return void
     */
    public void setVictory(){victory = true; onVictory();}

    @Override
    public boolean isOff() {return !victory;}
    @Override
    public boolean isOn() {return victory;}

    /**
     * Setter to modify the current area's key value. This is only used to block the player inside of BossArea
     * @param newKV the new key value to assign to the current area
     * @return void
     */
    public void setKeyVal(int newKV){
        //edits the key ID of a portal
        keyVal = newKV;
        for (AreaPortals dir : AreaPortals.values()){
            getPortal(dir).setKeyId(newKV);
        }
    }

    /**
     * Method, which creates a collectible Heart in the position of a previously present wall in the same spot
     * @param position the position, where the new heart will be created
     * @return void
     */
    public void replaceWallByHeart(DiscreteCoordinates position){
        //creates a heart, when a wall is broken, at a certain probability
        Heart newH = new Heart(this, position);
        addItem(newH);
    }
    /**
     * Getter for Tuto1's scale factor
     * @return float Scale factor in both the x-direction and the y-direction
     */
    @Override
    //public final float getCameraScaleFactor() {return 10f;}
    public float getCameraScaleFactor() {
        return (float) Math.min(getWidth() * DYNAMIC_SCALE_MULTIPLIER , MAXIMUM_SCALE);
    }

    /**
     * Method, which updates the current area
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    /**
     * Method, which defines what happens, when the current area is launched
     */
    public abstract void onVictory();
}
