package glm;


/**
 * A generic binary operator used in lambda calculus

 * `(a, b) -> c`
 */
@FunctionalInterface
public interface LambdaOperatorBinary <A, B, C>
{
    C apply(A a, B b);
}
