package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.icmaze.actor.collectable.Collectibles;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.LevelGenerator;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.engine.actor.Actor;
import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.signal.logic.Or;
import ch.epfl.cs107.play.window.Window;

import java.awt.geom.Area;

public class ICMaze extends AreaGame {
    private ICMazePlayer player;
    private boolean victory;
    private void createAreas(ICMazeArea[] areas){
        for (ICMazeArea area : areas){
            addArea(area);
        }
    }
    public Rock r;
    public boolean begin(Window window, FileSystem fileSystem){
        if (super.begin(window , fileSystem)) {
            //Initializes the playing field
            createAreas(LevelGenerator.generateLine(3));
            setCurrentArea("ICMaze/Spawn", true);
            player = new ICMazePlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(5,5));
            player.enterArea(getCurrentArea(), new DiscreteCoordinates(5,5));
            getCurrentArea().setViewCandidate(player);
            victory = false;
            return true;
        }
        else return false;
    }
    public void addItem(Actor Item){
        ((ICMazeArea) getCurrentArea()).addItem(Item);
    }
    public void removeItem(Actor Item){
        ((ICMazeArea) getCurrentArea()).removeItem(Item);
    }

    public void switchArea(Portal receivedPortal){
        //switches the player's area when stepping into a portal
        ((ICMazeArea) getCurrentArea()).clearList();
        getCurrentArea().unregisterActor(player);
        ((ICMazeArea) getCurrentArea()).removeItem(player);
        setCurrentArea(receivedPortal.getDestinationArea(), false);
        if (receivedPortal.getDestinationArea() == "ICMaze/Boss"){
            ((ICMazeArea) getCurrentArea()).setKeyVal(-1);
        }
        ((ICMazeArea) getCurrentArea()).renewList();
        player.enterArea(getCurrentArea(), receivedPortal.getDestinationCoordinates());
        ((ICMazeArea) getCurrentArea()).addItem(player);
        getCurrentArea().setViewCandidate(player);
    }

    public void end(){}
    @Override
    public String getTitle() {
        return "ICMaze";
    }
    @Override
    public void draw() {
        super.draw();
    }
    @Override
    public void update(float deltaTime) {
        if (getCurrentArea().getKeyboard().get(KeyBindings.RESET_GAME).isPressed() || player.getHealth() <= 0){
            // the game is reset, when the player presses the R key or dies
            begin(getWindow(), getFileSystem());
        }
        Portal receivedPortal = player.getCurrentPortal();
        if (receivedPortal != null){
            switchArea(receivedPortal);
            player.clearCurrentPortal();
        }
        victory = ((ICMazeArea) getCurrentArea()).isOn();
        super.update(deltaTime);
    }

    public void reset() {
    }
}