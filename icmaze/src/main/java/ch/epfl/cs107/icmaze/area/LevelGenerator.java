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
        if (length < MINIMAL_LENGTH){
            length = MINIMAL_LENGTH;
        }
        ICMazeArea[] areas = new ICMazeArea[length];
        Map<int[], ICMazeArea> levelSetup = new HashMap<>();
        int[] currentPos = {0,0};
        int currKeyVal = Integer.MAX_VALUE;
        levelSetup.put(currentPos, new Spawn(currKeyVal));
        for (int i = 1; i < length; i++){
            int[] nextLevelPos;
            do{
                nextLevelPos = getLevelPos(currentPos, getRandomPos());
            }while(contains(levelSetup, nextLevelPos) != null);
            ICMazeArea newArea;
            ICMazeArea previousArea = contains(levelSetup, currentPos);
            currKeyVal -= 1;
            if (i < length-1){
                double progress = (double) (i + 1) / length;
                newArea = areaCreation(progress, currKeyVal);
                newArea.setPreviousPortal(previousArea.getAreaSize(), previousArea.getTitle(), getrelativeDirection(nextLevelPos, currentPos));
            }
            else{
                newArea = new BossArea();
                newArea.setNextPortal(previousArea.getAreaSize(), previousArea.getTitle(), getrelativeDirection(nextLevelPos, currentPos));
            }
            previousArea.setNextPortal(newArea.getAreaSize(), newArea.getTitle(), getrelativeDirection(currentPos, nextLevelPos));
            levelSetup.put(nextLevelPos, newArea);
            currentPos = nextLevelPos;
        }
        int index = 0;
        for (ICMazeArea currArea : levelSetup.values()){
            areas[index] = currArea;
            index += 1;
        }
        return areas;
    }

    private static ICMazeArea areaCreation(double progress, int currKeyVal){
        double r = rng.nextDouble();
        int currDiff = getDiff(progress);
        if (r < progress * progress)
            return new LargeArea(currKeyVal, currDiff);
        if (r < progress)
            return new MediumArea(currKeyVal, currDiff);
        return new SmallArea(currKeyVal, currDiff);
    }

    private static ICMazeArea contains(Map<int[], ICMazeArea> list, int[] coords){
        for (Map.Entry<int[], ICMazeArea> pair : list.entrySet()){
            if (pair.getKey()[0] == coords[0] && pair.getKey()[1] == coords[1]){
                return pair.getValue();
            }
        }
        return null;
    }

    private static int getDiff(double progress){
        switch ((int) (progress * 4)) {
            case 0 -> {return Difficulty.EASY;}
            case 1 -> {return Difficulty.MEDIUM;}
            case 2 -> {return Difficulty.HARD;}
            default -> {return Difficulty.HARDEST;}
        }
    }

    private static int[] getLevelPos(int[] currentPos, ICMazeArea.AreaPortals newLevelDir){
        switch (newLevelDir){
            case N -> {return new int[]{currentPos[0], currentPos[1]+1};}
            case E -> {return new int[]{currentPos[0]+1, currentPos[1]};}
            case S -> {return new int[]{currentPos[0], currentPos[1]-1};}
            case null, default -> {return new int[]{};}
        }
    }

    private static ICMazeArea.AreaPortals getRandomPos(){
        List<ICMazeArea.AreaPortals> lst = new ArrayList<>();
        lst.add(ICMazeArea.AreaPortals.E);
        lst.add(ICMazeArea.AreaPortals.N);
        lst.add(ICMazeArea.AreaPortals.S);
        Collections.shuffle(lst, rng);
        return lst.get(0);
    }

    private static ICMazeArea.AreaPortals getrelativeDirection(int[] currentPos, int[] nextPos){
        int[] relPos = {nextPos[0]-currentPos[0], nextPos[1]-currentPos[1]};
        if (relPos[0] == 1 && relPos[1] == 0){
            return ICMazeArea.AreaPortals.E;
        }else if (relPos[0] == 0 && relPos[1] == 1){
            return ICMazeArea.AreaPortals.N;
        }else if (relPos[0] == 0 && relPos[1] == -1){
            return ICMazeArea.AreaPortals.S;
        }
        return ICMazeArea.AreaPortals.W;
    }
}
