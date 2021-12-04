package fr.uge.poo.cmdline.ex7;

/**
 * Interface for a bi-consumer.
 */
@FunctionalInterface
public interface IntBiConsumer {
    /**
     * Performs this operation on the given arguments.
     *
     * @param a the first integer argument to the operation
     * @param b the second integer argument to the operation
     */
    void accept(int a, int b);
}
