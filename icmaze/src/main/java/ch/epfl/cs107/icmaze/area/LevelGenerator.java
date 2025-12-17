package ch.epfl.cs107.icmaze.area;

import java.util.*;

import ch.epfl.cs107.icmaze.Difficulty;
import ch.epfl.cs107.icmaze.area.maps.*;

import static ch.epfl.cs107.icmaze.RandomGenerator.rng;

/**
 * Utility class for the procedural generation of a sequence of game levels, based on the wanted length.
 */
public class LevelGenerator {
    private final static int MINIMAL_LENGTH = 2; //the minimal possible number of levels in a game

    /**
     * method, which generates a table of connected ICMazeAreas, with the provided length
     * @param length the desired number of total game levels (default value is 2, 1x Spawn and 1x BossArea)
     * @return ICMazeArea[] - the table of connected ICMazeAreas created by this method
     */
    public static ICMazeArea[] generateLine(int length){
        //if the provided length is below MINIMAL_LENGTH, the method automatically uses that constant as the game length
        if (length < MINIMAL_LENGTH){
            length = MINIMAL_LENGTH;
        }
        ICMazeArea[] areas = new ICMazeArea[length]; //the table of ICMazeAreas that will be returned
        //Mapping of <int[] (level position), ICMazeArea (area at that position)>
        Map<int[], ICMazeArea> levelSetup = new HashMap<>();
        int[] currentPos = {0,0}; //the default position of the spawn area
        int currKeyVal = Integer.MAX_VALUE; //initial value of the key
        levelSetup.put(currentPos, new Spawn(currKeyVal));
        for (int i = 1; i < length; i++){
            int[] nextLevelPos;
            do{ //searches a position for a new area, that doesn't yet exist in the levelSetup mapping
                nextLevelPos = getLevelPos(currentPos, getRandomPos());
            }while(getAreafromCoords(levelSetup, nextLevelPos) != null);
            ICMazeArea newArea;
            ICMazeArea previousArea = getAreafromCoords(levelSetup, currentPos); //get the area from the previous iteration
            currKeyVal -= 1;
            if (i < length-1){ //creating maze areas
                double progress = (double) (i + 1) / length; //given progress formula for maze generation
                newArea = areaCreation(progress, currKeyVal);
                //initialize the newly created area's entry portal to lead to the previous area (OPEN)
                newArea.setPreviousPortal(previousArea.getAreaSize(), previousArea.getTitle(), getrelativeDirection(nextLevelPos, currentPos));
            } else{ // creating the boss' area
                newArea = new BossArea();
                //initialize the newly created BossArea's exit portal to lead to the previous area (CLOSED)
                newArea.setNextPortal(previousArea.getAreaSize(), previousArea.getTitle(), getrelativeDirection(nextLevelPos, currentPos));
            }
            //initialize the area of the previous iteration's exit portal (CLOSED)
            previousArea.setNextPortal(newArea.getAreaSize(), newArea.getTitle(), getrelativeDirection(currentPos, nextLevelPos));
            levelSetup.put(nextLevelPos, newArea);
            currentPos = nextLevelPos;
        }
        int index = 0;
        //puts every ICMazeArea from levelSetup into the areas table
        for (ICMazeArea currArea : levelSetup.values()){
            areas[index] = currArea;
            index += 1;
        }
        return areas;
    }

    /**
     * method, which randomly creates a MazeArea of a certain type, based on progress-based probabilities
     * along with the current key value for the given area
     * @param progress the progress value, calculated in the generateLine method
     * @param currKeyVal the current area's initial key value
     * @return ICMazeArea - the MazeArea that is created by this method
     */
    private static ICMazeArea areaCreation(double progress, int currKeyVal){
        double r = rng.nextDouble();
        int currDiff = getDiff(progress);
        //decide which type of MazeArea to create based on the progress-based difficulty
        if (r < progress * progress)
            return new LargeArea(currKeyVal, currDiff);
        if (r < progress)
            return new MediumArea(currKeyVal, currDiff);
        return new SmallArea(currKeyVal, currDiff);
    }

    /**
     * method, which gets the ICMazeArea associated with the proceeded coordinates in the game setup
     * @param list the Mapping of Map<int[], ICMazeArea> list
     * @param coords the int[] position of the current area in the game's level setup
     * @return ICMazeArea - the MazeArea that is associated with the provided coordinates (null if not present)
     */
    private static ICMazeArea getAreafromCoords(Map<int[], ICMazeArea> list, int[] coords){
        for (Map.Entry<int[], ICMazeArea> pair : list.entrySet()){
            //compares coordinates to decide if equal
            if (pair.getKey()[0] == coords[0] && pair.getKey()[1] == coords[1]){
                return pair.getValue();
            }
        }
        return null;
    }

    /**
     * method, which gets the difficulty value based on the player's progress
     * @param progress A double representing the player's progress
     * @return The next maze's difficulty value, an int
     */
    private static int getDiff(double progress){
        //gets the difficulty value based on a given formula
        switch ((int) (progress * 4)) {
            case 0 -> {return Difficulty.EASY;}
            case 1 -> {return Difficulty.MEDIUM;}
            case 2 -> {return Difficulty.HARD;}
            default -> {return Difficulty.HARDEST;}
        }
    }

    /**
     * method, which gets the position of the next game level in the game level setup,
     * based on the current area's position and the direction from the current area to the next area
     * ATTENTION - NO MAZES WESTWARD - ALL QUIET ON THE WESTERN FRONT
     * @param currentPos An int[] table, representing the current area's position
     * @param newLevelDir The direction of the portal, which leads from the current area to the next area
     * @return An int[], the position of the next area in the game levels setup
     */
    private static int[] getLevelPos(int[] currentPos, ICMazeArea.AreaPortals newLevelDir){
        int[] nextLevelPosition = {currentPos[0], currentPos[1]};
        switch (newLevelDir){
            //the next area's position is one step up from the current one, if it's up North to the current one
            case N -> nextLevelPosition[1] += 1;
            //the next area's position is one step right from the current one, if it's on the East to the current one
            case E -> nextLevelPosition[0] += 1;
            //the next area's position is one step down from the current one, if it's down South to the current one
            case S -> nextLevelPosition[1] -= 1;
        }
        return nextLevelPosition;
    }

    /**
     * method, which generates a random direction from the three available directions (N, E, S)
     * The return value may be invalid (Already present in the mappping)
     * Therefore requires an additional check for validity
     * @return ICMazeArea.AreaPortals - the random direction to the next area
     */
    private static ICMazeArea.AreaPortals getRandomPos(){
        List<ICMazeArea.AreaPortals> lst = new ArrayList<>();
        lst.add(ICMazeArea.AreaPortals.E);
        lst.add(ICMazeArea.AreaPortals.N);
        lst.add(ICMazeArea.AreaPortals.S);
        Collections.shuffle(lst, rng);
        return lst.get(0);
    }

    /**
     * method, which provides the relative direction between two neighboring ICMazeAreas.
     * One of the two is considered as the main area of the calculation
     * @param mainPos An int[], which gives the position of the main area for the calculation
     * @param nextPos An int[], which gives the position of the next area for the calculation
     * @return ICMazeArea.AreaPortals - the relative direction from the main area to the next area
     */
    private static ICMazeArea.AreaPortals getrelativeDirection(int[] mainPos, int[] nextPos){
        int[] relPos = {nextPos[0]-mainPos[0], nextPos[1]-mainPos[1]}; // relative position (final - initial)
        if (relPos[0] == 1 && relPos[1] == 0){ //if the relative position is equal to {1, 0}
            return ICMazeArea.AreaPortals.E;
        }else if (relPos[0] == 0 && relPos[1] == 1){ //if the relative position is equal to {0, 1}
            return ICMazeArea.AreaPortals.N;
        }else if (relPos[0] == 0 && relPos[1] == -1){ //if the relative position is equal to {0, -1}
            return ICMazeArea.AreaPortals.S;
        }
        return ICMazeArea.AreaPortals.W;  //if the relative position is equal to {1, 0}
    }
}
