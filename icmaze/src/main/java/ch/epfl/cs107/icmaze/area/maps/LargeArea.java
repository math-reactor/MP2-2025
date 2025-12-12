package ch.epfl.cs107.icmaze.area.maps;

import ch.epfl.cs107.icmaze.RandomGenerator;
import ch.epfl.cs107.icmaze.actor.LogMonster;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.PortalState;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.engine.actor.Background;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Orientation;

public class LargeArea extends MazeArea {

    public LargeArea(int setKey, int setDiff){
        super(setKey, setDiff, "LargeArea");
    }

    @Override
    public void createArea(){
        // 1) fond
        registerActor(new Background(this, name));

        // 2) génération du labyrinthe + clés
        super.createArea();
        super.randomKey(exitKey - 1);

        // 3) génération des LogMonster (pages 29–30)
        createLogMonsters();

        // 4) portails comme avant
        Portal westMAPortal = getPortal(AreaPortals.W);
        westMAPortal.setState(PortalState.OPEN);
        westMAPortal.setDestinationCoordinates(AreaPortals.W, "MediumArea");
        westMAPortal.setDestinationArea("ICMaze/MediumArea[" + (exitKey + 1) + "]");

        Portal eastMAPortal = getPortal(AreaPortals.E);
        eastMAPortal.setState(PortalState.LOCKED);
        eastMAPortal.setDestinationCoordinates(AreaPortals.E, "SmallArea");
        eastMAPortal.setDestinationArea("ICMaze/Boss");
    }

    private void createLogMonsters() {
        int maxEnemies = 3;      // plafond proposé dans le pdf
        int created    = 0;

        // version simple : on ne rebranche pas encore sur la vraie Difficulty,
        // on met diffRatio = 1.0 pour ne pas se prendre la tête maintenant.
        double diffRatio = 1.0;

        while (created < maxEnemies
                && RandomGenerator.rng.nextDouble() < 0.25 + 0.60 * diffRatio) {

            // position aléatoire sur le graphe (méthode utilitaire d’ICMazeArea)
            DiscreteCoordinates cell = randomGraphCell();

            // Choix de l’état initial (p.29)
            double pTarget   = 0.10 + 0.70 * diffRatio;
            double pRandom   = 0.20;
            double pSleeping = 1.0 - (pTarget + pRandom);

            double r = RandomGenerator.rng.nextDouble();
            LogMonster.State initState;

            if (r < pSleeping) {
                initState = LogMonster.State.SLEEPING;
            } else if (r < pSleeping + pRandom) {
                initState = LogMonster.State.WANDERING;
            } else {
                initState = LogMonster.State.CHASING;
            }

            LogMonster monster =
                    new LogMonster(this, Orientation.DOWN, cell, initState) {
                        @Override
                        protected void setOrientation(Orientation o) {

                        }

                        @Override
                        public void interactWith(Interactable other, boolean isCellInteraction) {

                        }
                    };
            addItem(monster);
            created++;
        }
    }

    @Override
    public String getTitle() {
        return "ICMaze/LargeArea[" + exitKey + "]";
    }
}