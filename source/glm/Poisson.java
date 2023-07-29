package glm;


/**
 * Poisson distribution General Linear Model (GLM) for a fill rank model.
 *
 * <ul>
 *     <li> Uses the log function as a link function (log-linear regression).
 *     <li> Performs Iterative reWeighted Least Squares (IWLS) to find the linear coefficients of the explanatory
 *          variables.
 * </ul>
 */
public class Poisson extends Distribution {

    private static final double NORMALISATION_EPSILON = 0.00001;


    /**
     * Instantiates and fits a Poisson distribution General Linear Model (GLM).
     *
     * @param ys Response variable vector.
     * @param xs Explanatory variable matrix.
     * @throws ArithmeticException Distribution data must be in a valid mathematical form for GLM.
     *                             <ul>
     *                                 <li> `ys` and `xs` must have the same number of rows.
     *                                 <li> `ys` must be a vector (single column).
     *                                 <li> `xs` cannot have more columns than rows.
     *                                 <li> `ys` must be non-negative (not checked, but will cause errors).
     *                             </ul>
     */
    public Poisson(Matrix ys, Matrix xs) throws ArithmeticException {
        super(ys, xs);
        super.estiBetas();
    }


    /*
     * Calculates the eta vector using the link function.
     *
     * Uses log function as link function.
     * eta = g(mu) = log(mu).
     *
     * @param mus Current expected value vector.
     * @return Eta vector.
     */
     Matrix linkFunc(Matrix mus) {
        return mus.getMap(Math::log);
    }


    /*
     * Calculates the result of link function derivative.
     *
     * Uses log function as link function.
     * g'(mu) = 1 / mu.
     * `mus` is normalised to prevent a zero division error.
     *
     * @param mus Current expected value vector.
     * @return Result of link function derivative.
     */
     Matrix linkFuncDiff(Matrix mus) {
        mus = mus.getMap(Poisson::muNormalisation);
        return mus.getMap(mu -> 1 / mu);
    }


    /*
     * Calculates the expected value vector using the link function inverse.
     *
     * Uses log function as link function.
     * mu = g^(-1)(eta) = e^(eta).
     *
     * @param etas Current eta vector.
     *                 eta = g(mu) = X * beta
     * @return Expected value vector.
     */
     Matrix linkFuncInv(Matrix etas) {
        return etas.getMap(Math::exp);
    }


    /*
     * Calculates the result of variance function.
     *
     * Uses log function as link function.
     * v(mu) = b''(b'^(-1)(mu)) = mu.
     * `mus` is normalised to prevent a zero division error.
     *
     * @param mus Current expected value vector.
     * @return Result of variance function.
     */
     Matrix varFunc(Matrix mus) {
        mus = mus.getMap(Poisson::muNormalisation);
        return mus;
    }


    /*
     * Calculates the log likelihood.
     *
     * Uses log function as link function.
     * The log factorial component is included even though it cancels out when comparing log likelihoods.
     * logLike = y * log(mu) - mu - log(y!).
     *
     * @param ys Response vector.
     * @param mus Expected values vector.
     * @return Log likelihood.
     */
     double logLike(Matrix ys, Matrix mus) {
        Matrix logProbs = ys.getZip(mus, (y, mu) -> y * Math.log(mu) - mu - Poisson.logFactorial(y.intValue()));
        return logProbs.getFoldVec(Double::sum, 0.0);
    }


    /*
     * Prevents `mu` (expected value) from being exactly zero.
     *
     * @param mu Single expected value for an observation.
     * @return Normalised `mu`.
     */
    private static double muNormalisation(double mu) {
        // exact float comparison as error only occurs when exactly zero
        if (mu == 0) {
            mu += NORMALISATION_EPSILON;
        }
        return mu;
    }


    /*
     * Calculates the log factorial.
     *
     * @param number The n'th log factorial to calculate.
     * @return `number` log factorial.
     * @throws ArithmeticException Factorials only exist for non-negatives.
     */
    private static double logFactorial(int number) throws ArithmeticException {

        if (number < 0) {
            throw new ArithmeticException("Cannot get the factorial of a negative number.");
        }

        double result = Math.log(1);
        for (int factor = 2; factor <= number; factor++) {
            result += Math.log(factor);
        }

        return result;
    }
}
