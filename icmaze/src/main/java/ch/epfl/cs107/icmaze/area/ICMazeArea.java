package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ICMazeArea extends Area {
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
    private final Portal[] portals = new Portal[AreaPortals.values().length];
    private ArrayList <Entity> runThrough; //Contains all entities in this area, for easy access
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

    private void initPortals() {
        for (AreaPortals ap : AreaPortals.values()) {
            DiscreteCoordinates mainCell;
            switch (ap) {
                case N -> mainCell = new DiscreteCoordinates(size / 2 - 1, size-1);
                case S -> mainCell = new DiscreteCoordinates(size / 2 - 1, 0);
                case W -> mainCell = new DiscreteCoordinates(0, size / 2 - 1);
                case E -> mainCell = new DiscreteCoordinates(size, size / 2);
                default -> throw new IllegalStateException();
            }

            DiscreteCoordinates arrival;

            switch (ap) {
                case N -> arrival = new DiscreteCoordinates(size / 2, 0);
                case S -> arrival = new DiscreteCoordinates(size / 2, size);
                case W -> arrival = new DiscreteCoordinates(0, size / 2 - 1);
                case E -> arrival = new DiscreteCoordinates(size, size / 2);
                default -> throw new IllegalStateException();
            }

            Orientation spriteOrientation = ap.getOrientation().opposite();

            String destAreaName = null;
            int keyId = Portal.NO_KEY_ID;

            Portal portal = new Portal(this, spriteOrientation, mainCell, destAreaName, arrival, keyId);

            portals[ap.ordinal()] = portal;
            addItem(portal);
        }
    }

    /**
     * Area specific callback to initialise the instance
     */
    protected abstract void createArea();
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
            createArea();
            initPortals();
            return true;
        }
        return false;
    }
    public void addItem(Entity Item){
        //adds the created items into the list
        runThrough.add((Entity) Item);
        this.registerActor(Item);
    }
    public void removeItem(Entity Item){
        //removes the item from the list
        for (int i = 0; i < runThrough.size(); i++){
            if (runThrough.get(i).equals((Entity) Item)){
                this.unregisterActor(Item);
                runThrough.remove(i);
            }
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
    /**
     * Getter for Tuto1's scale factor
     * @return Scale factor in both the x-direction and the y-direction
     */
    @Override
    //public final float getCameraScaleFactor() {return 10f;}
    public float getCameraScaleFactor() {
        return (float) Math.min(getWidth() * DYNAMIC_SCALE_MULTIPLIER , MAXIMUM_SCALE);
    }
}
