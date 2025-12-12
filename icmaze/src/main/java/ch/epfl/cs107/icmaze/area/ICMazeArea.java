package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Health;
import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.maps.BossArea;
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
    public ArrayList<Actor> runThrough; //Contains all entities in this area, for easy access
    public ArrayList<Health> healthbars = new ArrayList<>();;
    public final static float DEFAULT_SCALE_FACTOR = 11.f;
    public final static float DYNAMIC_SCALE_MULTIPLIER = 1.375f;
    public final static float MAXIMUM_SCALE = 30.f;
    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    private Window window;
    private AreaGraph graph;
    private String name;
    private int size;

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
        return portals[direction.ordinal()];
    }

    public void setPreviousPortal(String setPrevAreaSize, String setPrevArea, AreaPortals setPrevDir){
        //sets the open (exit) portal of the current area
        prevAreaSize = setPrevAreaSize;
        prevArea = setPrevArea;
        prevDir = setPrevDir;
    }

    public void setNextPortal(String setNextAreaSize, String setNextArea, AreaPortals setNextDir){
        //sets the closed (next) portal of the current area
        nextAreaSize = setNextAreaSize;
        nextArea = setNextArea;
        nextDir = setNextDir;
    }

    private void initPortals() {
        //initlailzes all four portals in the current area
        for (AreaPortals ap : AreaPortals.values()) {
            DiscreteCoordinates mainCell;
            switch (ap) {
                case N -> mainCell = new DiscreteCoordinates(size / 2 - 1, size-1);
                case S -> mainCell = new DiscreteCoordinates(size / 2 - 1, 0);
                case W -> mainCell = new DiscreteCoordinates(0, size / 2 - 1);
                case E -> mainCell = new DiscreteCoordinates(size-1, size / 2-1);
                default -> throw new IllegalStateException();
            }
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

    public void addItem(Actor Item){
        //adds the created items into the area
        runThrough.add((Actor) Item);
        this.registerActor(Item);
    }

    public void renewList(){
        //registers all the actors of the current area
        for (Actor actor : runThrough){
            this.registerActor(actor);
            enterAreaCells((Interactable) actor, ((Interactable) actor).getCurrentCells());
        }
    }

    public void removeItem(Actor Item){
        //removes the given item from the list
        for (int i = 0; i < runThrough.size(); i++){
            if (runThrough.get(i).equals((Actor) Item)){
                this.unregisterActor(Item);
                purgeAreaCellsFrom((Interactable) runThrough.get(i));
                runThrough.remove(i);
                break;
            }
        }
    }

    public void clearList(){
        //removes all actors in the current area
        int maxSize = runThrough.size();
        for (int i = 0; i < maxSize; i++){
            this.unregisterActor(runThrough.get(i));
            purgeAreaCellsFrom((Interactable) runThrough.get(i));
        }
    }

    public String getAreaSize(){return name;}
    protected int getKeyVal(){return keyVal;}

    public void setKeyVal(int newKV){
        //edits the key ID of a portal
        keyVal = newKV;
        for (AreaPortals dir : AreaPortals.values()){
            getPortal(dir).setKeyId(newKV);
        }
    }
    protected void createGraph(){
        graph = new AreaGraph();
    }
    protected void createNode(int row, int col, boolean up, boolean left, boolean down, boolean right){
        //creates a pathfinding node for the graph
        graph.addNode(new DiscreteCoordinates(col, row),left, up,right,down);
    }
    protected void randomKey(int keyID){
        //creates a key at a random spot
        List lst = graph.keySet();
        Collections.shuffle(lst, RandomGenerator.rng);
        DiscreteCoordinates randomCoords = (DiscreteCoordinates) lst.get(0);
        addItem(new Key(this, Orientation.DOWN, randomCoords, keyID));
    }

    public void replaceWallByHeart(DiscreteCoordinates position){
        //creates a heart, when a wall is broken, at a certain probability
        Heart newH = new Heart(this, position);
        addItem(newH);
    }
    /**
     * Getter for Tuto1's scale factor
     * @return Scale factor in both the x-direction and the y-direction
     */
    @Override
    //public final float getCameraScaleFactor() {return 10f;}
    public float getCameraScaleFactor() {
        return (float) Math.min(getWidth() * DYNAMIC_SCALE_MULTIPLIER , MAXIMUM_SCALE);
    }
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}
