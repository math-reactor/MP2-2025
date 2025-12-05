package ch.epfl.cs107.icmaze;

import ch.epfl.cs107.icmaze.actor.ICMazeActor;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.collectable.Collectibles;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.icmaze.area.maps.*;
import ch.epfl.cs107.play.areagame.AreaGame;
import ch.epfl.cs107.play.engine.actor.Entity;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Window;

public class ICMaze extends AreaGame {
    private ICMazeActor player;
    private void createAreas(){
        addArea(new BossArea());
        addArea(new Spawn());
        addArea(new SmallArea(Integer.MAX_VALUE, Difficulty.HARDEST));
        addArea(new MediumArea(Integer.MAX_VALUE, Difficulty.HARDEST));
        addArea(new LargeArea(Integer.MAX_VALUE, Difficulty.HARDEST));
    }
    public boolean begin(Window window, FileSystem fileSystem){
        if (super.begin(window , fileSystem)) {
            createAreas();
            //setCurrentArea("ICMaze/Spawn", true);
            setCurrentArea("ICMaze/LargeArea["+Integer.MAX_VALUE+"]", true);
            player = new ICMazePlayer(getCurrentArea(), Orientation.DOWN, new DiscreteCoordinates(5,5));
            addItem(player); //adds the player to the area
            player.enterArea(getCurrentArea(), new DiscreteCoordinates(5,5));
            getCurrentArea().setViewCandidate(player);

            return true;
        }
        else return false;
    }
    public void addItem(Entity Item){
        ((ICMazeArea) getCurrentArea()).addItem(Item);
    }
    public void removeItem(Entity Item){
        ((ICMazeArea) getCurrentArea()).removeItem(Item);
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
}