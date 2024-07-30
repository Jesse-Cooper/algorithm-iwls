package glm;


import java.lang.Math;


/**
 * Gaussian General Linear Model (GLM)

 * Uses the identity function as a link function
     * `g(x) = x`
     * Identity function is the canonical link for Gaussian
     * Models fitted are standard linear regression models
 */
public class Gaussian extends Distribution
{
    // * Variance of the response is assumed to homoscedastic, so it is constant
    //   across all observations (known variance)
    private static final double VARIANCE = 1;

    private static final String DISTRIBUTION_NAME = "Gaussian";
    private static final String LINK_FUNC_NAME = "Identity function";


    // * Used in the log-likelihood
    private final double constantTerm;


    /**
     * Instantiates and fits a Gaussian GLM

     * @param ys
         * Response vector
     * @param xs
         * Explanatory matrix
     */
    public Gaussian(
        final Matrix ys,
        final Matrix xs)
    {
        super(ys, xs, DISTRIBUTION_NAME, LINK_FUNC_NAME);

        constantTerm = - (ys.getNRows() * Math.log(2 * Math.PI * VARIANCE)) / 2;

        super.estiBetas();
    }


    /**
     * Calculates the current linear predictor of each observation (`etas`) from
       the current expected value of each observation (`mus`) using the link
       function

     * `etas = g(mus) = mus`

     * @param mus
         * Current expected value of each observation
     * @return
         * Current linear predictor of each observation (`etas`)
     */
    Matrix linkFunc(final Matrix mus)
    {
        return mus;
    }


    /**
     * Calculates the current link function derivative from the current expected
       value of each observation (`mus`)

     * `g'(mus) = 1`

     * @param mus
         * Current expected value of each observation
     * @return
         * Current value of the link function derivative
     */
    Matrix linkFuncDiff(final Matrix mus)
    {
        return mus.mapMatrix(mu -> 1.0);
    }


    /**
     * Calculates the current expected value of each observation (`mus`) from
       the current linear predictor of each observation (`etas`) using the link
       function inverse

     * `mus = g^(-1)(etas) = 1 / eta`

     * @param etas
         * Current linear predictor of each observation
         * `etas = xs * betas`
     * @return
         * Current expected value of each observation (`mus`)
     */
    Matrix linkFuncInv(final Matrix etas)
    {
        return etas.mapMatrix(eta -> Numerical.isEqual(eta, 0) ? 0 : 1 / eta);
    }


    /**
     * Calculates the current variance function from the current expected value
       of each observation (`mus`)

     * Variance function is the effect of an expected value on its observation's
       variance
     * `v(mus) = b''(b'^(-1)(mus)) = 1`

     * @param mus
         * Current expected value of each observation
     * @return
         * Current value of the variance function
     */
    Matrix varFunc(final Matrix mus)
    {
        return mus.mapMatrix(x -> 1.0);
    }


    /**
     * Calculates the current log-likelihood of the current model parameters

     * `logLike = C - sum((ys - mus)^2) / (2 * sigma^2)`
         * `C = - n * log(2 * pi * sigma^2) / 2`
     * Constant term (`C`) is included even though it cancels out when comparing
       log-likelihoods

     * @param ys
         * Response vector
     * @param mus
         * Current expected value of each observation
     * @return
         * Current log-likelihood of the current model parameters
     */
    double logLike(
        final Matrix ys ,
        final Matrix mus)
    {
        final Matrix residualSquares;
        final double RSS;

        residualSquares = ys.zipMatrix(mus, (y, mu) -> (y - mu) * (y - mu));
        RSS = residualSquares.foldVec(Double::sum, 0.0);

        return constantTerm - (RSS / (2 * VARIANCE));
    }
}
