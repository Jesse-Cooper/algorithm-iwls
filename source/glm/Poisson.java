package glm;


import java.lang.Math;


/**
 * Poisson General Linear Model (GLM)

 * Uses the log function as a link function
     * `g(x) = log_e(x)`
     * Log function is the canonical link for Poisson
     * Models fitted are log-linear regression models
 */
public class Poisson extends Distribution
{
    // * Used to prevent zero division errors when using the expected values of
    //   the observations (`mus`)
    private static final double NORMALISATION_EPSILON = 1e-4;

    private static final String DISTRIBUTION_NAME = "Poisson";
    private static final String LINK_FUNC_NAME = "Log function";


    /**
     * Instantiates and fits a Poisson GLM

     * @param ys
         * Response vector
     * @param xs
         * Explanatory matrix
     */
    public Poisson(
        final Matrix ys,
        final Matrix xs)
    {
        super(ys, xs, DISTRIBUTION_NAME, LINK_FUNC_NAME);
        super.estiBetas();
    }


    /**
     * Calculates the current linear predictor of each observation (`etas`) from
       the current expected value of each observation (`mus`) using the link
       function

     * `etas = g(mus) = log_e(mus)`

     * @param mus
         * Current expected value of each observation
     * @return
         * Current linear predictor of each observation (`etas`)
     */
    Matrix linkFunc(final Matrix mus)
    {
        return mus.mapMatrix(Math::log);
    }


    /**
     * Calculates the current link function derivative from the current expected
       value of each observation (`mus`)

     * `g'(mus) = 1 / mus`
     * Expected values in `mus` are normalised to prevent zero division errors

     * @param mus
         * Current expected value of each observation
     * @return
         * Current value of the link function derivative
     */
    Matrix linkFuncDiff(Matrix mus)
    {
        mus = mus.mapMatrix(Poisson::muNormalisation);
        return mus.mapMatrix(mu -> 1 / mu);
    }


    /**
     * Calculates the current expected value of each observation (`mus`) from
       the current linear predictor of each observation (`etas`) using the link
       function inverse

     * `mus = g^(-1)(etas) = e^(etas)`

     * @param etas
         * Current linear predictor of each observation
         * `etas = xs * betas`
     * @return
         * Current expected value of each observation (`mus`)
     */
    Matrix linkFuncInv(final Matrix etas)
    {
        return etas.mapMatrix(Math::exp);
    }


    /**
     * Calculates the current variance function from the current expected value
       of each observation (`mus`)

     * Variance function is the effect of an expected value on its observation's
       variance
     * `v(mus) = b''(b'^(-1)(mus)) = mus`
     * Expected values in `mus` are normalised to prevent zero division errors

     * @param mus
         * Current expected value of each observation
     * @return
         * Current value of the variance function
     */
    Matrix varFunc(Matrix mus)
    {
        mus = mus.mapMatrix(Poisson::muNormalisation);
        return mus;
    }


    /**
     * Calculates the current log-likelihood of the current model parameters

     * `logLike = sum(ys * log_e(mus) - mus - C)`
         * `C = log_e(ys!)`
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
        final Matrix logProbs;

        logProbs = ys.zipMatrix(mus, Poisson::singleLogLike);
        return logProbs.foldVec(Double::sum, 0.0);
    }


    /**
     * Prevents the current expected value of an observation (`mu`) from being
       `0`

     * @param mu
         * Current expected value of an observation
     * @return
         * Normalised `mu`
     */
    private static double muNormalisation(double mu)
    {
        // * Compare floats exactly as the error only occurs when exactly `0`
        if (mu == 0)
        {
            mu += NORMALISATION_EPSILON;
        }
        return mu;
    }


    /**
     * Calculates the current log-likelihood of a single observation

     * @param y
         * Observation to calculate log-likelihood of
     * @param mu
         * Current expected value of observation `y`
     * @return
         * Current log-likelihood of `y`
     */
    private static double singleLogLike(
        final double y ,
        final double mu)
    {
        return y * Math.log(mu) - mu - logFactorial(y);
    }


    /**
     * Calculates the log_e factorial

     * @param number
         * n'th log_e factorial to calculate
     * @return
         * `number` log_e factorial
     * @throws ArithmeticException
         * Cannot get the factorial of a negative number
         * `number >= 0`
     */
    private static double logFactorial(
        final double number)
    throws ArithmeticException
    {
        int factor;
        double result;

        if (number < 0)
        {
            throw new ArithmeticException(
                "\n\n"
                + "* Cannot get the factorial of a negative number\n"
                + "* `number` must be non-negative\n"
                + "* `number = " + number + "`\n"
            );
        }

        result = 0;
        for (factor = 2; factor <= number; factor += 1)
        {
            result += Math.log(factor);
        }
        return result;
    }
}
