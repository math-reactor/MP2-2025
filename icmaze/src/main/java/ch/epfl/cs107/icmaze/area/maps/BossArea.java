package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.actor.Boss.ICMazeBoss;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;
import ch.epfl.cs107.play.window.Canvas;

/**
 * Class, which allows for the creation of the boss' area
 */
public class BossArea extends ICMazeArea{
    private ICMazeBoss boss; //the boss that is in this area

    /**
     * Constructor for the BossArea class, initializes its name in the ICMazeArea superclass
     */
    public BossArea(){
        super("SmallArea", -1);
    }

    /**
     * Method for the graphic initialization of the BossArea, creating the background and the boss object
     */
    public void createArea(){
        registerActor(new Background(this, getAreaSize())); //background
        boss = new ICMazeBoss(this); //creation of the boss
        super.addItem(boss);
    }

    /**
     * Method for the final elimination of the boss, after its cloud animation is over
     */
    public void killBoss(){
        DiscreteCoordinates bossPos = boss.getCurrentMainCellCoordinates();
        removeItem(boss); //finally removes the boss
        //creates a key in the boss' death position
        Key newKey = new Key(this, Orientation.DOWN, bossPos, -1);
        addItem(newKey);
    }
    /**
     * Empty placeholder method for what happens after the victory condition is met (player collects key)
     * Nothing out of the ordinary happens in BossArea, when the game is beaten
     */
    public void onVictory(){}

    /**
     * Returns the title of the area
     * @return String - the title of the area, its unique ID
     */
    public String getTitle(){return "ICMaze/Boss";}

    @Override
    /**
     * draws the bossArea on the Canvas
     * @param canvas the canvas that is used for the drawing
     */
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

}
