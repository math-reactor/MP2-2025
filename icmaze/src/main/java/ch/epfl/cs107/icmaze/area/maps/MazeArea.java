package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public abstract class MazeArea extends ICMazeArea {
    //private AreaPortals entrance;
    protected int exitKey;
    private int difficulty;
    private int[][] mazeGrid;
    public MazeArea(int setExitKey, int setDiff, String name){
        super(name);
        super.createGraph();
        exitKey = setExitKey;
        difficulty = setDiff;
    }
    public void createArea(){
        mazeGrid = MazeGenerator.createMaze(getWidth()-2, getHeight()-2, difficulty);
        int add = 0;
        for (int row = 1; row < getHeight()-1; row++){
            add += 1;
            for (int col = 1; col < getWidth()-1; col++){
                if (mazeGrid[row-1][col-1] == 1 && !checkAtEntrance(new DiscreteCoordinates(col,row))){
                    addItem(new Rock(this, new DiscreteCoordinates(col,row)));
                }
                else{
                    setupNode(mazeGrid, row, col);
                }
            }
        }
    }
    private boolean checkAtEntrance(DiscreteCoordinates wallTile){
        boolean isAtEntrance = false;
        for (AreaPortals orient : AreaPortals.values()){
            DiscreteCoordinates arrivalTile = new DiscreteCoordinates(0,0);
            switch(orient){
                case AreaPortals.N -> arrivalTile = new DiscreteCoordinates(getWidth() / 2, 1);
                case AreaPortals.S -> arrivalTile = new DiscreteCoordinates(getWidth() / 2, getHeight() - 2);
                case AreaPortals.W -> arrivalTile = new DiscreteCoordinates(getWidth() - 2, getHeight() / 2);
                case AreaPortals.E -> arrivalTile = new DiscreteCoordinates(1, getHeight() / 2);
            }
            isAtEntrance = arrivalTile.equals(wallTile);
        }
        return isAtEntrance;
    }

    public void makePointWalkable(DiscreteCoordinates coords){
        setupNode(mazeGrid, coords.y, coords.x);
    }

    private void setupNode(int[][] grid, int row, int col){
        boolean right = true;
        boolean left = true;
        boolean down = true;
        boolean up = true;
        if (col == 1 || (col != 1 && grid[row-1][col-2] == 1)){
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
        super.createNode(row, col, up, left, down, right);
    }
}
