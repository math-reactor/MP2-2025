##Classes/Interfaces ajoutées

On a ajouté une interface Damageable à tous les acteurs qui peuvent être endommagés,
afin qu'ils puissent avoir un type en commun (Utile lors de la destruction des labyrinthes
et la mise en 'sommeil' des LogMonster après la victoire du joueur)

##modification à l'architecture du jeu

Notre équivalent de la méthode generateLine a l'entête suivante :

public static ICMazeArea[] generateLine(int length)     au lieu de

public static ICMazeArea[] generateLine(ICMaze game , int length)

On a ajouté un nombre maximal de LogMonster proportionnel à la taille du MazeArea courant

On a aussi décidé d'utiliser les projectiles de feu au lieu d'utilisser des projectiles d'eau

Les points de vie sont modélisés par ICMazeActor, sauf pour les Rock, qui ne sont pas des ICMazeActor,
car ils ne peuvent pas se déplacer

##extensions ajoutées

Pause du jeu à l'aide d'un dialogue "pause", peut être accédé par la toucht P