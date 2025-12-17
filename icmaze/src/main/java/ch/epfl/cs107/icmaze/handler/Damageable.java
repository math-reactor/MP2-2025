package ch.epfl.cs107.icmaze.handler;

/**
 * Interface, which is applied to all damageable objects, to give them all a common type
 */
public interface Damageable {
    /**
     * the function all damageable actors have in common
     */
    void beAttacked(int damage);
}
