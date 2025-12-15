package ch.epfl.cs107.icmaze.area;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.Size;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.play.areagame.AreaGraph;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Logic;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class MazeArea extends ICMazeArea {
    private final DiscreteCoordinates[] inaccessibleEntranceCells;
    private final int MAX_ENEMIES = 3;

    private int difficulty;
    private int[][] mazeGrid;
    private boolean victory;
    private AreaGraph graph;

    public MazeArea(int setExitKey, int setDiff, String name){
        super(name, setExitKey);
        graph = new AreaGraph();
        difficulty = setDiff;
        victory = false;
        int size = Size.getSize(name);
        inaccessibleEntranceCells = new DiscreteCoordinates[]{
                new DiscreteCoordinates(size / 2 + 1, 1), //lower entrance
                new DiscreteCoordinates(size / 2, 1),
                new DiscreteCoordinates(size / 2 + 1, size), //upper entrance
                new DiscreteCoordinates(size / 2, size),
                new DiscreteCoordinates(size, size / 2 + 1), //right entrance
                new DiscreteCoordinates(size, size / 2),
                new DiscreteCoordinates(1, size / 2 + 1), //left entrance
                new DiscreteCoordinates(1, size / 2)
        };
    }
    public void createArea(){
        mazeGrid = MazeGenerator.createMaze(getWidth()-2, getHeight()-2, difficulty);
        //sets up the labyrinth using the table with 0s and 1s
        for (int row = 1; row < getHeight()-1; row++){
            for (int col = 1; col < getWidth()-1; col++){
                if (mazeGrid[row-1][col-1] == 1){
                    addItem(new Rock(this, new DiscreteCoordinates(col,row)));
                }
                else{
                    if (isNotEntrance(new DiscreteCoordinates(col,row))){
                        setupNode(mazeGrid, row, col);
                    }
                }
            }
        }
    }

    public void occupyCell(DiscreteCoordinates currPos, DiscreteCoordinates nextPos){
        if (graph.keySet().contains(currPos)){
            graph.setSignal(currPos, Logic.TRUE);
        }
        if (graph.keySet().contains(nextPos)){
            graph.setSignal(nextPos, Logic.FALSE);
        }
    }

    private boolean isNotEntrance(DiscreteCoordinates newLoc){
        boolean accessible = true;
        for (DiscreteCoordinates inaccessible : inaccessibleEntranceCells){
            if (inaccessible.equals(newLoc)){
                accessible = false;
                break;
            }
        }
        return accessible;
    }

    public boolean isAccessible(DiscreteCoordinates newLoc){
        return graph.keySet().contains(newLoc);
    }

    public Queue<Orientation> shortestPath(DiscreteCoordinates currentpos, DiscreteCoordinates newPos){
        return graph.shortestPath(currentpos, newPos);
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

    public void makePointWalkable(DiscreteCoordinates coords){
        setupNode(mazeGrid, coords.y, coords.x);
    }

    private void setupNode(int[][] grid, int row, int col){
        boolean right = true;
        boolean left = true;
        boolean down = true;
        boolean up = true;
        if (col == 1 || col != 1 && (grid[row-1][col-2] == 1)){ //the second condition checks if the neighboring cells are walls or not
            left = false;
        }
        if (col == grid[0].length || (col != grid[0].length && grid[row-1][col] == 1)) {
            right = false;
        }
        if (row == grid.length || (row != grid.length &&grid[row][col-1] == 1)){
            up = false;
        }
        if (row == 1 || (row != 1 && grid[row-2][col-1] == 1)) {
            down = false;
        }
        createNode(row, col, up, left, down, right);
    }
    @Override
    public boolean isOff() {
        return !victory;
    }
    @Override
    public boolean isOn() {
        return victory;
    }
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
