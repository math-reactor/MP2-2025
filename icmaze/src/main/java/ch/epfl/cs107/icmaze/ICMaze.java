package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.LevelGenerator;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.engine.actor.Dialog;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

/**
 * Class, which represents the ICMaze game
 */
public class ICMaze extends AreaGame {
    private final int LEVEL_LENGTH = 3; //the length of the game level, minimum value is 0
    private ICMazePlayer player;
    private boolean victory;
    private boolean hasVictoryDialog;

    private static Dialog currentDialog;
    private static Dialog pauseDialog;
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
            //runs the introductory dialog
            currentDialog = new Dialog("welcome");
            hasVictoryDialog = false;
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
            if (victory == false){
                currentDialog = new Dialog("life_for_life");
            }
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

    /**
     * method, which returns whether a dialog is being run through
     * @return boolean - whether the game is going through a dialog
     */
    public static boolean runningDialog(){return currentDialog != null;}

    /**
     * method, which runs the key_required dialog, when the player doesn't have the right key to open a portal
     */
    public static void noKeyDialog(){currentDialog = new Dialog("key_required");}

    /**
     * method, which runs the key_required dialog, when the player doesn't have the right key to open a portal
     */
    public static boolean isGamePaused(){return pauseDialog != null;}

    /**
     * method, returns the area's name
     * @return the area's name, a String
     */
    @Override
    public String getTitle() {
        //returns the area's title
        return "ICMaze";
    }
    /**
     * method, draws the current area
     */
    @Override
    public void draw() {
        //draws the game on the canvas
        super.draw();
        if (currentDialog != null){
            currentDialog.draw(getWindow());
        }
        if (pauseDialog != null){
            pauseDialog.draw(getWindow());
        }
    }
    /**
     * method, which updates the current area
     * @param deltaTime time variation
     * @return void
     */
    @Override
    public void update(float deltaTime) {
        boolean hasDied = false;
        if (getCurrentArea().getKeyboard().get(KeyBindings.RESET_GAME).isPressed() || player.getHealth() <= 0){
            // the game is reset, when the player presses the R key or dies
            hasDied = player.getHealth() <= 0;
            begin(getWindow(), getFileSystem());
            if (hasDied){
                currentDialog = new Dialog("defeat");
            }
        }
        if (getCurrentArea().getKeyboard().get(KeyBindings.NEXT_DIALOG).isPressed() && currentDialog != null){
            // the next slide of the Dialog is displayed
            currentDialog.update(deltaTime);
            if (currentDialog.isCompleted()){
                currentDialog = null;
            }
        }
        if (getCurrentArea().getKeyboard().get(KeyBindings.PAUSE_GAME).isPressed() && currentDialog == null){
            // the next slide of the Dialog is displayed
            if (pauseDialog != null){
                pauseDialog.update(deltaTime);
                pauseDialog = null;
            }else{
                pauseDialog = new Dialog("pause");
            }
        }
        //switches the area, when the player steps into a portal
        Portal receivedPortal = player.getCurrentPortal();
        if (receivedPortal != null){
            switchArea(receivedPortal);
            player.clearCurrentPortal();
        }
        //sets the victory value, if the player beats the bodd
        victory = ((ICMazeArea) getCurrentArea()).isOn();
        if (victory){
            if (!hasVictoryDialog){
                hasVictoryDialog = true;
                currentDialog = new Dialog("victory");
            }
        }
        super.update(deltaTime);
    }
}