package ch.epfl.cs107.icmaze.handler;

import ch.epfl.cs107.icmaze.actor.Boss.ICMazeBoss;
import ch.epfl.cs107.icmaze.actor.ICMazePlayer;
import ch.epfl.cs107.icmaze.actor.enemies.LogMonster;
import ch.epfl.cs107.icmaze.actor.Portal;
import ch.epfl.cs107.icmaze.actor.Rock;
import ch.epfl.cs107.icmaze.actor.collectable.Heart;
import ch.epfl.cs107.icmaze.actor.collectable.Key;
import ch.epfl.cs107.icmaze.actor.collectable.Pickaxe;
import ch.epfl.cs107.icmaze.area.ICMazeBehaviour;
import ch.epfl.cs107.play.areagame.actor.Interactable;
import ch.epfl.cs107.play.areagame.handler.AreaInteractionVisitor;

/**
 * InteractionVisitor for the ICMaze entities
 */
public interface ICMazeInteractionVisitor extends AreaInteractionVisitor {
    /// Add Interaction method with all non Abstract Interactable

    /**
     * the default function for an interaction with a Key
     */
    default void interactWith(Key key, boolean isCellInteraction){};

    /**
     * the default function for an interaction with a Pickaxe
     */
    default void interactWith(Pickaxe pickaxe, boolean isCellInteraction){};

    /**
     * the default function for an interaction with a Heart
     */
    default void interactWith(Heart heart, boolean isCellInteraction){};

    /**
     * the default function for an interaction with a cell
     */
    default void interactWith(ICMazeBehaviour.ICMazeCell cell, boolean isCellInteraction){};

    /**
     * the default function for an interaction with a Portal
     */
    default void interactWith(Portal portal, boolean isCellInteraction){};

    /**
     * the default function for an interaction with a Rock
     */
    default void interactWith(Rock rock, boolean isCellInteraction){};

    /**
     * the default function for an interaction with an ICMazePlayer
     */
    default void interactWith(ICMazePlayer player, boolean isCellInteraction){};

    /**
     * the default function for an interaction with an ICMazeBoss
     */
    default void interactWith(ICMazeBoss boss, boolean isCellInteraction){};

    /**
     * the default function for an interaction with a LogMonster
     */
    default void interactWith(LogMonster logMonster, boolean isCellInteraction){};

    /**
     * the default function for an interaction with a basic Interactable
     */
    default void interactWith(Interactable other, boolean isCellInteraction){};
}
