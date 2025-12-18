package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.Size;
import ch.epfl.cs107.icmaze.actor.enemies.LogMonster;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

/**
 * Abstract class, which represents the concept of a basic area with a maze in this game
 */
public abstract class MazeArea extends ICMazeArea {
    //constants
    private final int MAX_ENEMIES;
    private final int difficulty;
    private final AreaGraph graph;

    private int[][] mazeGrid; //the grid associated with theMazeArea 1's for walls, 0^s for free spots

    /**
     * Constructor for the MazeArea class, initializes the given area by creating a Graph,
     * by setting the maximal number of enemies and by setting the area's difficulty level
     * @param setExitKey the key value needed to exit this area (int)
     * @param setDiff the key value of this ICMazeArea (int)
     * @param name the name of this MazeArea, which is associated to its size (String)
     */
    public MazeArea(int setExitKey, int setDiff, String name){
        super(name, setExitKey);
        graph = new AreaGraph();
        difficulty = setDiff;
        MAX_ENEMIES = Size.getSize(name)/4;
    }

    /**
     * Method, which creates this MazeArea's maze, creating the area's maze grid with 0's and 1's.
     * The 0's symbolize free spots and the 1's in the table symbolize walls to be placed.
     * This method also initializes the
     */
    public void createArea(){
        //creates a random grid using the MazeGenerator (1's for walls, 0's for free spots)
        mazeGrid = MazeGenerator.createMaze(getWidth()-2, getHeight()-2, difficulty);
        //sets up the labyrinth using the table with 0s and 1s
        for (int row = 1; row < getHeight()-1; row++){
            for (int col = 1; col < getWidth()-1; col++){
                //we use row-1 and col-1, to account for the index offset of the table (table starts at 0, MazeArea at 1)
                if (mazeGrid[row-1][col-1] == 1){
                    addItem(new Rock(this, new DiscreteCoordinates(col,row)));
                }
                else{ //creates a node, when int[row-1][col-1] == 0
                    graph.addNode(new DiscreteCoordinates(col, row), true, true, true, true);
                }
            }
        }
    }

    /**
     * Method, which handles what happens after a victors - In the case of a MazeArea,
     * all present non-player ICMazeActors are exterminated with prejudice
     */
    public void onVictory(){
        killAll();
    }

    /**
     * Method, which returns a boolean, based on whether a cell is present in the MazeArea's graph
     * @param newLoc the provided DiscreteLocation
     * @return boolean whether the provided DiscreteLocation is accessible (not blocked by a wall)
     */
    public boolean isAccessible(DiscreteCoordinates newLoc){
        return graph.keySet().contains(newLoc);
    }

    /**
     * Method, which provides the shortest path between two DiscretePositions
     * @param currentpos the DiscretePosition representing the starting position
     * @param newPos the DiscretePosition representing the end position
     * @return Queue<Orientation>, a Queue of all the necessary orientations, which the Pathfinder needs to take, to reach its destination
     */
    public Queue<Orientation> shortestPath(DiscreteCoordinates currentpos, DiscreteCoordinates newPos){
        return graph.shortestPath(currentpos, newPos);
    }

    /**
     * Method, which creates a key on a random free spot on the graph associated to this area
     * @param keyID the ID associated to the newly created key
     */
    protected void randomKey(int keyID){
        //creates a key at a random spot
        List lst = graph.keySet();
        Collections.shuffle(lst, RandomGenerator.rng);
        DiscreteCoordinates randomCoords = (DiscreteCoordinates) lst.get(0);
        addItem(new Key(this, randomCoords, keyID));
    }

    /**
     * Method, which creates a graph node on a given position (Usually used, when a rock is destroyed)
     * @param coords the DiscreteCoordinates, where thenode will be created at
     */
    public void makePointWalkable(DiscreteCoordinates coords){
        graph.addNode(coords, true, true, true, true);
    }

    /**
     * Method, which creates the LogMonsters that will roam this area. Their numbers are determined by MAX_ENEMIES.
     */
    protected void createLogMonsters() {
        // version simple : on ne rebranche pas encore sur la vraie Difficulty,
        // on met diffRatio = 1.0 pour ne pas se prendre la tête maintenant.
        double diffRatio = Math.min(1.0, (double) Difficulty.HARDEST / difficulty);
        for (int number = 0; number < MAX_ENEMIES; number++){
            if (RandomGenerator.rng.nextDouble() < 0.25 + 0.60 * diffRatio){
                // position aléatoire sur le graphe (méthode utilitaire d’ICMazeArea)
                List lst = graph.keySet();
                Collections.shuffle(lst, RandomGenerator.rng);
                DiscreteCoordinates randomCoords = (DiscreteCoordinates) lst.get(0);

                // Choix de l’état initial (p.29)
                double pTarget   = 0.10 + 0.70 * diffRatio;
                double pRandom   = 0.20;
                double pSleeping = 1.0 - (pTarget + pRandom);

                double r = RandomGenerator.rng.nextDouble();
                LogMonster.State initState;

                //différentes probabilités d'être crée dans des différents états
                if (r < pSleeping) {
                    initState = LogMonster.State.SLEEPING;
                } else if (r < pSleeping + pRandom) {
                    initState = LogMonster.State.WANDERING;
                } else {
                    initState = LogMonster.State.CHASING;
                }

                LogMonster monster = new LogMonster(this, Orientation.DOWN, randomCoords, initState);
                addItem(monster);
            }
        }
    }
}
