package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
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
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ICMazeArea extends Area {
    private final Portal[] portals = new Portal[AreaPortals.values().length];
    public ArrayList <Actor> runThrough; //Contains all entities in this area, for easy access
    public final static float DEFAULT_SCALE_FACTOR = 11.f;
    public final static float DYNAMIC_SCALE_MULTIPLIER = 1.375f;
    public final static float MAXIMUM_SCALE = 30.f;
    private float cameraScaleFactor = DEFAULT_SCALE_FACTOR;
    private Window window;
    private AreaGraph graph;
    protected String name;
    private int size;

    public ICMazeArea(String setName){
        runThrough = new ArrayList<>();
        name = setName;
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

    private void initPortals() {
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
            int keyId = Integer.MAX_VALUE;
            DiscreteCoordinates defaultSpawn = new DiscreteCoordinates(1,1);

            Portal portal = new Portal(this, spriteOrientation, mainCell, destAreaName, defaultSpawn, keyId);

            portals[ap.ordinal()] = portal;
            addItem(portal);
        }
    }

    public void addItem(Actor Item){
        //adds the created items into the list
        runThrough.add((Actor) Item);
        this.registerActor(Item);
    }

    public void renewList(){
        for (Actor actor : runThrough){
            this.registerActor(actor);
            this.
            enterAreaCells((Interactable) actor, ((Interactable) actor).getCurrentCells());
        }
    }

    public void removeItem(Actor Item){
        //removes the item from the list
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
        int maxSize = runThrough.size();
        for (int i = 0; i < maxSize; i++){
            this.unregisterActor(runThrough.get(i));
            purgeAreaCellsFrom((Interactable) runThrough.get(i));
        }
    }
    protected void createGraph(){
        graph = new AreaGraph();
    }
    protected void createNode(int row, int col, boolean up, boolean left, boolean down, boolean right){
        graph.addNode(new DiscreteCoordinates(col, row),left, up,right,down);
    }
    protected void randomKey(int keyID){
        List lst = graph.keySet();
        Collections.shuffle(lst, RandomGenerator.rng);
        DiscreteCoordinates randomCoords = (DiscreteCoordinates) lst.get(0);
        addItem(new Key(this, Orientation.DOWN, randomCoords, keyID));
    }

    public void replaceWallByHeart(DiscreteCoordinates position){
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
