package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.MazeGenerator;
import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.area.ICMazeArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public abstract class MazeArea extends ICMazeArea {

    protected final int exitKey;
    /** niveau de difficulté de cette aire (1, 2, 3, …) */
    private final int difficulty;

    /** nombre maximal de monstres troncs par labyrinthe (pdf p.29) */
    private static final int MAX_LOG_MONSTERS = 3;
    /** valeur max utilisée pour normaliser la difficulté (à adapter si besoin) */
    private static final double MAX_DIFFICULTY = 3.0;

    public MazeArea(int setExitKey, int setDiff, String name){
        super(name);
        createGraph();
        this.exitKey = setExitKey;
        this.difficulty = setDiff;
    }

    @Override
    public void createArea(){
        int[][] template = MazeGenerator.createMaze(getWidth()-2, getHeight()-2, difficulty);

        for (int row = 1; row < getHeight()-1; row++){
            for (int col = 1; col < getWidth()-1; col++){
                DiscreteCoordinates coords = new DiscreteCoordinates(col, row);

                if (template[row-1][col-1] == 1 && !checkAtEntrance(coords)) {
                    // mur : on place un rocher
                    addItem(new Rock(this, coords));
                } else {
                    // case libre : on crée un noeud dans le graphe
                    createNode(template, row, col);
                }
            }
        }

        // ➜ une fois le labyrinthe généré, on ajoute les LogMonster
        spawnLogMonsters();
    }

    /** true si ce mur correspond à une entrée/sortie d’aire (on ne met pas de rocher ici). */
    private boolean checkAtEntrance(DiscreteCoordinates wallTile){
        for (AreaPortals orient : AreaPortals.values()){
            DiscreteCoordinates arrivalTile;
            switch(orient){
                case N -> arrivalTile = new DiscreteCoordinates(getWidth() / 2, 1);
                case S -> arrivalTile = new DiscreteCoordinates(getWidth() / 2, getHeight() - 2);
                case W -> arrivalTile = new DiscreteCoordinates(getWidth() - 2, getHeight() / 2);
                case E -> arrivalTile = new DiscreteCoordinates(1, getHeight() / 2);
                default -> throw new IllegalStateException("Unexpected value: " + orient);
            }
            if (arrivalTile.equals(wallTile)) {
                return true;
            }
        }
        return false;
    }

    /** Crée un noeud de graphe pour une case libre. */
    private void createNode(int[][] grid, int row, int col){
        boolean left  = col != 0;
        boolean right = col != grid[0].length-1;
        boolean up    = row != 0;
        boolean down  = row != grid.length-1;

        super.createNode(row, col, up, left, down, right);
    }

    // -------------------------------------------------------------------------
    //                PARTIE DEMANDÉE PAGES 29–30 : LogMonster
    // -------------------------------------------------------------------------

    /** Ajoute des LogMonster à des positions aléatoires du labyrinthe. */
    protected void spawnLogMonsters() {
        // Heuristique du pdf : plus la difficulté est grande, plus diffRatio est grand.
        double diffRatio = Math.min(1.0, difficulty / MAX_DIFFICULTY);

        // 1) Déterminer combien d’ennemis on veut (jusqu’à MAX_LOG_MONSTERS)
        int monstersToCreate = 0;
        while (monstersToCreate < MAX_LOG_MONSTERS) {
            double threshold = 0.25 + 0.60 * diffRatio; // pdf p.29
            if (RandomGenerator.rng.nextDouble() < threshold) {
                monstersToCreate++;
            } else {
                break;
            }
        }

        // 2) Créer les ennemis à des positions aléatoires du graphe
        for (int i = 0; i < monstersToCreate; i++) {
            DiscreteCoordinates pos = randomGraphCell();
            LogMonster.State initialState = pickInitialState(diffRatio);

            Orientation randomOrientation =
                    Orientation.values()[RandomGenerator.rng.nextInt(Orientation.values().length)];

            LogMonster monster = new LogMonster(this, randomOrientation, pos, initialState) {
                @Override
                protected void setOrientation(Orientation o) {

                }
            };
            addItem(monster);
        }
    }

    protected DiscreteCoordinates randomGraphCell() {
        return null;
    }

    /** Choisit l’état initial du LogMonster (pdf p.29). */
    private LogMonster.State pickInitialState(double diffRatio) {
        double pTarget   = 0.10 + 0.70 * diffRatio; // déplacement ciblé
        double pRandom   = 0.20;                    // déplacement aléatoire
        double pSleeping = 1.0 - (pTarget + pRandom);

        double r = RandomGenerator.rng.nextDouble();
        if (r < pSleeping) {
            return LogMonster.State.SLEEPING;
        } else if (r < pSleeping + pRandom) {
            return LogMonster.State.WANDERING;
        } else {
            return LogMonster.State.CHASING;
        }
    }
}