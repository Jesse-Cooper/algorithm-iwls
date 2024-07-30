package glm;


/**
 * A generic unary operator used in lambda calculus

 * `a -> b`
 */
@FunctionalInterface
public interface LambdaOperatorUnary <A, B>
{
    B apply(A a);
}
