package glm;


/*
 * A generic binary operation used in lambda calculus.
 *
 * (a, b) -> c.
 */
@FunctionalInterface
interface BinaryOperator<A, B, C> {
    C apply(A a, B b);
}
