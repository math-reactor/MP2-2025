package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.area.Area;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

public abstract class ICMazeArea extends Area {

    private final Portal[] portals = new Portal[AreaPortals.values().length];

    public final ArrayList<Actor> runThrough = new ArrayList<>(); // accès facile (ok)
    public static final float DEFAULT_SCALE_FACTOR = 11.f;
    public static final float DYNAMIC_SCALE_MULTIPLIER = 1.375f;
    public static final float MAXIMUM_SCALE = 30.f;

    private Window window;
    private AreaGraph graph;
    protected String name;
    private int size;

    public ICMazeArea(String setName){
        name = setName;
    }

    /** Area specific callback to initialise the instance */
    public abstract void createArea();

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        this.window = window;
        if (super.begin(window, fileSystem)) {
            setBehavior(new ICMazeBehaviour(window, name));
            size = getWidth();
            initPortals();
            createArea();
            return true;
        }
        return false;
    }

    // ---------------- PATHFINDING (p.29) ----------------
    public Queue<Orientation> shortestPath(DiscreteCoordinates from, DiscreteCoordinates to) {
        if (graph == null) return null;
        return graph.shortestPath(from, to);
    }

    /** Choisit une cellule aléatoire valide du graphe (pour spawn / clés / monstres). */
    protected DiscreteCoordinates randomGraphCell() {
        if (graph == null || graph.keySet().isEmpty()) {
            throw new IllegalStateException("Graph not initialized or empty: cannot pick random graph cell");
        }
        List<DiscreteCoordinates> cells = new ArrayList<>(graph.keySet());
        Collections.shuffle(cells, RandomGenerator.rng);
        return cells.get(0);
    }

    public enum AreaPortals {
        N(Orientation.UP),
        W(Orientation.LEFT),
        S(Orientation.DOWN),
        E(Orientation.RIGHT);

        private final Orientation orientation;
        AreaPortals(Orientation orientation) { this.orientation = orientation; }
        public Orientation getOrientation() { return orientation; }
    }

    public Portal getPortal(AreaPortals direction){
        return portals[direction.ordinal()];
    }

    private void initPortals() {
        for (AreaPortals ap : AreaPortals.values()) {
            DiscreteCoordinates mainCell;
            switch (ap) {
                case N -> mainCell = new DiscreteCoordinates(size / 2 - 1, size - 1);
                case S -> mainCell = new DiscreteCoordinates(size / 2 - 1, 0);
                case W -> mainCell = new DiscreteCoordinates(0, size / 2 - 1);
                case E -> mainCell = new DiscreteCoordinates(size - 1, size / 2 - 1);
                default -> throw new IllegalStateException();
            }

            Orientation spriteOrientation = ap.getOrientation();
            String destAreaName = null;
            int keyId = Integer.MAX_VALUE;
            DiscreteCoordinates defaultSpawn = new DiscreteCoordinates(1, 1);

            Portal portal = new Portal(this, spriteOrientation, mainCell, destAreaName, defaultSpawn, keyId);
            portals[ap.ordinal()] = portal;
            addItem(portal);
        }
    }

    public void addItem(Actor item){
        runThrough.add(item);
        registerActor(item);
    }

    public void renewList(){
        for (Actor actor : runThrough){
            registerActor(actor);
            if (actor instanceof Interactable interactable) {
                enterAreaCells(interactable, interactable.getCurrentCells());
            }
        }
    }

    public void removeItem(Actor item){
        for (int i = 0; i < runThrough.size(); i++){
            if (runThrough.get(i).equals(item)){
                unregisterActor(item);
                if (runThrough.get(i) instanceof Interactable interactable) {
                    purgeAreaCellsFrom(interactable);
                }
                runThrough.remove(i);
                break;
            }
        }
    }

    public void clearList(){
        for (Actor actor : runThrough){
            unregisterActor(actor);
            if (actor instanceof Interactable interactable) {
                purgeAreaCellsFrom(interactable);
            }
        }
        runThrough.clear();
    }

    // ---------------- GRAPH ----------------
    protected void createGraph(){
        graph = new AreaGraph();
    }

    protected void createNode(int row, int col, boolean up, boolean left, boolean down, boolean right){
        // Attention: AreaGraph addNode(pos, left, up, right, down) (ordre engine)
        graph.addNode(new DiscreteCoordinates(col, row), left, up, right, down);
    }

    protected void randomKey(int keyID){
        DiscreteCoordinates randomCoords = randomGraphCell();
        addItem(new Key(this, Orientation.DOWN, randomCoords, keyID));
    }

    public void replaceWallByHeart(DiscreteCoordinates position){
        Heart newH = new Heart(this, position, 24);
        addItem(newH);
    }

    @Override
    public float getCameraScaleFactor() {
        return (float) Math.min(getWidth() * DYNAMIC_SCALE_MULTIPLIER, MAXIMUM_SCALE);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }
}