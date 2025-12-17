package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.LevelGenerator;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

/**
 * Class, which represents the ICMaze game
 */
public class ICMaze extends AreaGame {
    private final int LEVEL_LENGTH = 3; //the length of the game level, minimum value is two
    private ICMazePlayer player;
    private boolean victory;
    /**
     * method, which initializes the areas, based on an array of areas from the level generator
     * @param areas A ICMazeArea[] table, which is run through, to initialize all the areas
     * @return void
     */
    private void createAreas(ICMazeArea[] areas){
        for (ICMazeArea area : areas){
            addArea(area);
        }
    }

    /**
     * method, which initializes the game
     * @param window the game window
     * @param fileSystem the game's filesystem
     * @return boolean, which tells you whether the game's setup has been successful or not
     */
    public boolean begin(Window window, FileSystem fileSystem){
        if (super.begin(window , fileSystem)) {
            //Initializes the playing field
            createAreas(LevelGenerator.generateLine(LEVEL_LENGTH));
            setCurrentArea("ICMaze/Spawn", true);
            //creation of a new player object
            player = new ICMazePlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(5,5));
            player.enterArea(getCurrentArea(), new DiscreteCoordinates(5,5));
            getCurrentArea().setViewCandidate(player);
            victory = false;
            return true;
        }
        else return false;
    }

    /**
     * method, which switches the player's current area
     * @param receivedPortal the portal where the player steps into
     * @return void
     */
    public void switchArea(Portal receivedPortal){
        //switches the player's area when stepping into a portal
        getCurrentArea().unregisterActor(player);
        ((ICMazeArea) getCurrentArea()).removeItem(player, true);
        //clears the area's actor list and switches the game's current area
        ((ICMazeArea) getCurrentArea()).clearList();
        setCurrentArea(receivedPortal.getDestinationArea(), false);
        //sets the key value of the next area to -1, if that area is a BossArea
        if (receivedPortal.getDestinationArea() == "ICMaze/Boss"){
            ((ICMazeArea) getCurrentArea()).setKeyVal(-1);
        }
        //if the victory condition is met, the individual areas will implement their victory methods
        if (victory){
            ((ICMazeArea) getCurrentArea()).setVictory();
        }
        //regenerates the current area's items and then spawns the player
        ((ICMazeArea) getCurrentArea()).renewList();
        player.enterArea(getCurrentArea(), receivedPortal.getDestinationCoordinates());
        ((ICMazeArea) getCurrentArea()).addItem(player, true);
        getCurrentArea().setViewCandidate(player);
    }

    /**
     * method, which is run at the closure of the game window - does nothing
     */
    public void end(){}

    @Override
    /**
     * method, returns the area's name
     * @return the area's name, a String
     */
    public String getTitle() {
        //returns the area's title
        return "ICMaze";
    }
    @Override
    /**
     * method, draws the current area
     */
    public void draw() {
        //draws the game on the canvas
        super.draw();
    }
    @Override
    /**
     * method, which updates the current area
     * @param deltaTime time variation
     * @return void
     */
    public void update(float deltaTime) {
        if (getCurrentArea().getKeyboard().get(KeyBindings.RESET_GAME).isPressed() || player.getHealth() <= 0){
            // the game is reset, when the player presses the R key or dies
            begin(getWindow(), getFileSystem());
        }
        //switches the area, when the player steps into a portal
        Portal receivedPortal = player.getCurrentPortal();
        if (receivedPortal != null){
            switchArea(receivedPortal);
            player.clearCurrentPortal();
        }
        //sets the victory value, if the player beats the bodd
        victory = ((ICMazeArea) getCurrentArea()).isOn();
        super.update(deltaTime);
    }
}