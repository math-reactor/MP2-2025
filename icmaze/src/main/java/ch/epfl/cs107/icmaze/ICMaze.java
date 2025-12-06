package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.*;
import ch.epfl.cs107.icmaze.actor.collectable.Collectibles;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
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
    private void createAreas(){
        addArea(new BossArea());
        addArea(new Spawn());
        addArea(new SmallArea(Integer.MAX_VALUE, Difficulty.HARDEST));
        addArea(new MediumArea(Integer.MAX_VALUE-1, Difficulty.HARDEST));
        addArea(new LargeArea(Integer.MAX_VALUE-2, Difficulty.HARDEST));
    }
    public Rock r;
    public boolean begin(Window window, FileSystem fileSystem){
        if (super.begin(window , fileSystem)) {
            createAreas();
            setCurrentArea("ICMaze/Spawn", true);
            player = new ICMazePlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(5,5));
            player.enterArea(getCurrentArea(), new DiscreteCoordinates(5,5));
            getCurrentArea().setViewCandidate(player);
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
        ((ICMazeArea) getCurrentArea()).clearList();
        getCurrentArea().unregisterActor(player);
        setCurrentArea(receivedPortal.getDestinationArea(), false);
        ((ICMazeArea) getCurrentArea()).renewList();
        player.enterArea(getCurrentArea(), receivedPortal.getDestinationCoordinates());
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
        super.update(deltaTime);
        Portal receivedPortal = player.getCurrentPortal();
        if (receivedPortal != null){
            switchArea(receivedPortal);
            player.clearCurrentPortal();
        }
    }
}