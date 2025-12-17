package ch.epfl.cs107.icmaze;

import java.util.Random;

import ch.epfl.cs107.play.math.DiscreteCoordinates;

/**
 * Utility class for generating rectangular mazes using the recursive division algorithm.
 * Provides additional helpers to ensure solvability and visualize the maze.
 */
public final class MazeGenerator {
    private static final int WALL = 1;
    private static final Random random = RandomGenerator.rng;

    private MazeGenerator(){}

    /**
     * method, which generates a maze pattern with 1's as walls and 0's as clear spots
     * @param height the height of the area, in terms of DiscreteCoordinates
     * @param width the width of the area, in terms of DiscreteCoordinates
     * @param difficulty the difficulty coefficient of the labyrinth area
     * @return int[][], a two-dimensional table with the size of the window, with 1's as walls and 0's as free spots
     */
    public static int [][] createMaze(int width , int height , int difficulty){
        int[][] grid = new int[height][width];
        if (width <= difficulty || height <= difficulty){
            return grid;
        }
        else{
            int wallPos;
            int holePos;
            int branch = random.nextInt(2); //default branch for the switch statement is chosen randomly, when width == height
            if (width < height){
                branch = 1;//horizontal wall
            } else if (width > height){
                branch = 0;//vertical wall
            }
            switch (branch) {
                case 0: {
                    // In the case of a vertical wall, we choose a random odd column, which is filled up with 1's
                    wallPos = randomOdd(width);
                    holePos = randomEven(height); //only at an even position along the wall, will we let a free spot
                    for (int i = 0; i < height; i++){
                        if (i != holePos){
                            grid[i][wallPos] = 1;
                        }
                    }
                    //creation of the two sub-mazes, which will merge into the end result
                    int[][] leftMaze = createMaze(wallPos, height, difficulty);
                    int[][] rightMaze = createMaze(width - wallPos - 1, height, difficulty);
                    return mergeVert(leftMaze, rightMaze, grid); //divide and conquer to create final maze
                }
                case 1: {
                    // In the case of a vertical wall, we choose a random odd row, which is filled up with 1's
                    wallPos = randomOdd(height);
                    holePos = randomEven(width); //only at an even position along the wall, will we let a free spot
                    for (int i = 0; i < width; i++){
                        if (i != holePos){
                            grid[wallPos][i] = 1;
                        }
                    }
                    //creation of the two sub-mazes, which will merge into the end result
                    int[][] TopMaze = createMaze(width, wallPos, difficulty);
                    int[][] BottomMaze = createMaze(width, height - wallPos-1, difficulty);
                    return mergeHor(TopMaze, BottomMaze, grid); //divide and conquer to create final maze
                }
            }
        }
        return grid;
    }

    /**
     * method, which merges two maze segments, which are separated by a vertical wall
     * @param leftMaze the int[][] table, which represents the maze on the left side to be merged
     * @param rightMaze the int[][] table, which represents the maze on the right side to be merged
     * @param basis the int[][] table, which represents the base maze, which will be the recipient of the merges
     * @return int[][] table, which is the result of the merging
     */
    public static int[][] mergeVert(int[][] leftMaze, int[][] rightMaze, int[][] basis){
        //merges two mazes, that are separated by a vertical wall
        for (int row = 0; row < leftMaze.length; row++){
            for (int col = 0; col < leftMaze[0].length; col++){
                basis[row][col] = leftMaze[row][col]; //sets the basis' row and col values as the left maze's ones
            }
        }
        int offset = leftMaze[0].length+1; //number of colums of leftmaze + 1, to move beyond the basis maze's only wall
        for (int row = 0; row < rightMaze.length; row++){
            for (int col = 0; col < rightMaze[0].length; col++){
                basis[row][offset + col] = rightMaze[row][col]; //sets the basis' row and col values as the right maze's ones
            }
        }
        return basis;
    }

    /**
     * method, which merges two maze segments, which are separated by a horizontal wall
     * @param topMaze the int[][] table, which represents the maze on the top side to be merged
     * @param bottomMaze the int[][] table, which represents the maze on the bottom side to be merged
     * @param basis the int[][] table, which represents the base maze, which will be the recipient of the merges
     * @return int[][] table, which is the result of the merging
     */
    public static int[][] mergeHor(int[][] topMaze, int[][] bottomMaze, int[][] basis){
        //merges two mazes, that are separated by a horizontal wall
        for (int row = 0; row < topMaze.length; row++){
            for (int col = 0; col < topMaze[0].length; col++){
                basis[row][col] = topMaze[row][col]; //sets the basis' row and col values as the top maze's ones
            }
        }
        int offset = topMaze.length+1; //number of lines of topmaze + 1, to move beyond the basis maze's only wall
        for (int row = 0; row < bottomMaze.length; row++){
            for (int col = 0; col < bottomMaze[0].length; col++){
                basis[offset + row][col] = bottomMaze[row][col]; //sets the basis' row and col values as the bottom maze's ones
            }
        }
        return basis;
    }
    /**
     * Print the maze
     */
    public static void printMaze(int[][] grid, DiscreteCoordinates start, DiscreteCoordinates end) {
        int height = grid.length;
        int width = grid[0].length;

        // Print top border
        System.out.print("┌");
        for (int i = 0; i < width; i++) {
            System.out.print("─-─");
        }
        System.out.println("┐");

        // Print maze rows
        for (int y = 0; y < height; y++) {
            System.out.print(y);
            for (int x = 0; x < width; x++) {
                if (x == start.x && y == start.y) System.out.print(" S ");
                else if (x == end.x && y == end.y) System.out.print(" E ");
                else System.out.print(grid[y][x] == WALL ? "███" : "   ");
            }
            System.out.println("│");
        }

        // Print bottom border
        System.out.print("└");
        for (int i = 0; i < width; i++) {
            System.out.print("───");
        }
        System.out.println("┘");
    }

    /**
     * Returns a random odd number in [1, max] (assuming max > 0).
     */
    private static int randomOdd(int max) {
        // is redefined, as it returns values from [0, max], with max inclusive, which poses problems
        int retVal = 0;
        do{
            retVal = 1 + 2 * random.nextInt((max + 1) / 2);
        } while(!ensureExclusive(max, retVal));
        return retVal;
    }
    private static boolean ensureExclusive(int max, int randomVal){
        //ensures that the entered value is not the maximum possible value, to avoid having walls at the portals
        if (max != randomVal){
            if (max % 2 == 0 && randomVal == max-1){
                return false;
            }
            return true;
        }
        return false;
    }
    /**
     * Returns a random even number in [0, max] (assuming max >= 0).
     */
    private static int randomEven(int max) {
        // doesn't need additional definitions, as it already returns values from [0, max[
        return 2 * random.nextInt((max + 1) / 2);
    }

}

