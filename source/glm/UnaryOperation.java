package glm;


/*
 * A generic unary operation used in lambda calculus.
 *
 * a -> b.
 */
@FunctionalInterface
interface UnaryOperation<A, B> {
    B apply(A a);
}
