package glm;


/**
 * Binomial distribution General Linear Model (GLM) for a fill rank model.
 *
 * <ul>
 *     <li> Uses the logistic function as a link function (logistic regression).
 *     <li> Performs Iterative reWeighted Least Squares (IWLS) to find the linear coefficients of the explanatory
 *          variables.
 * </ul>
 */
public class Binomial extends Distribution {

    private static final double NORMALISATION_EPSILON = 0.00001;

    // binomial distribution requires an extra parameter (number of trails per observation)
    private final Matrix ms;


    /**
     * Instantiates and fits Binomial distribution General Linear Model (GLM).
     *
     * @param ys Response variable vector.
     * @param xs Explanatory variable matrix.
     * @param ms Trails per response vector observation.
     * @throws ArithmeticException Distribution data must be in a valid mathematical form for GLM.
     *                             <ul>
     *                                 <li> `ys` and `xs` must have the same number of rows.
     *                                 <li> `ys` must be a vector (single column).
     *                                 <li> `xs` cannot have more columns than rows.
     *                                 <li> `ys` and `ms` must have the same number of rows.
     *                                 <li> `ms` must be a vector (single column).
     *                                 <li> `ys` must be non-negative no larger than `ms` (not checked, but will cause
     *                                       errors).
     *                             </ul>
     */
    public Binomial(Matrix ys, Matrix xs, Matrix ms) throws ArithmeticException {
        super(ys, xs);

        if (ys.getNRows() != ms.getNRows()) {
            throw new ArithmeticException("`ys` and `ms` must have the same number of rows.");
        }

        if (ms.getNCols() != 1) {
            throw new ArithmeticException("`ms` must be a vector (single column).");
        }

        this.ms = ms;

        super.estiBetas();
    }


    /*
     * Calculates the eta vector using the link function.
     *
     * Uses logistic function as link function.
     * eta = g(mu) = log(mu) - log(m - mu).
     * `mus` is normalised to prevent a zero division error.
     *
     * @param mus Current expected value vector.
     * @return Eta vector.
     */
     Matrix linkFunc(Matrix mus) {
        mus = mus.getZip(ms, Binomial::muNormalisation);
        return mus.getZip(ms, (mu, m) -> Math.log(mu) - Math.log(m - mu));
    }


    /*
     * Calculates the result of link function derivative.
     *
     * Uses logistic function as link function.
     * g'(mu) = m / (mu * (m - mu)).
     * `mus` is normalised to prevent a zero division error.
     *
     * @param mus Current expected value vector.
     * @return Result of link function derivative.
     */
     Matrix linkFuncDiff(Matrix mus) {
        mus = mus.getZip(ms, Binomial::muNormalisation);
        return mus.getZip(ms, (mu, m) -> m / (mu * (m - mu)));
    }


    /*
     * Calculates the expected value vector using the link function inverse.
     *
     * Uses logistic function as link function.
     * mu = g^(-1)(eta) = m / (1 + e^(-eta)).
     *
     * @param etas Current eta vector.
     *                 eta = g(mu) = X * beta
     * @return Expected value vector.
     */
    Matrix linkFuncInv(Matrix etas) {
        return etas.getZip(ms, (eta, m) -> m / (1 + Math.exp(-eta)));
    }


    /*
     * Calculates the result of variance function.
     *
     * Uses logistic function as link function.
     * v(mu) = b''(b'^(-1)(mu)) = mu * (1 - mu / m).
     * `mus` is normalised to prevent a zero division error.
     *
     * @param mus Current expected value vector.
     * @return Result of variance function.
     */
    Matrix varFunc(Matrix mus) {
        mus = mus.getZip(ms, Binomial::muNormalisation);
        return mus.getZip(ms, (mu, m) -> mu * (1 - mu / m));
    }


    /*
     * Calculates the log likelihood.
     *
     * Uses logistic function as link function.
     * The combinations component is included even though it cancels out when comparing log likelihoods.
     * logLike = y * log(p) + (m - y) * log(1 - p) + log(m C y).
     *
     * @param ys Response vector.
     * @param mus Expected values vector.
     * @return Log likelihood.
     */
    double logLike(Matrix ys, Matrix mus) {

        Matrix ps = mus.getZip(ms, (mu, m) -> mu / m);

        // calculate the log probabilities of success and failure
        Matrix psLog = ps.getMap(Math::log);
        Matrix qsLog = ps.getMap(p -> Math.log(1 - p));

        // calculate log components of the log likelihood
        Matrix successLogProbs = ys.getZip(psLog, (y, p) -> y * p);
        Matrix failureLogProbs = ms.getZip(ys, (m, y) -> m - y).getZip(qsLog, (mMinusY, q) -> mMinusY * q);
        Matrix combinationLogs = ms.getZip(ys, (m, y) -> Math.log(Binomial.combinations(m.intValue(), y.intValue())));

        Matrix logProbs = successLogProbs.getZip(failureLogProbs, Double::sum).getZip(combinationLogs, Double::sum);

        // sum the log probabilities for each observation
        return logProbs.getFoldVec(Double::sum, 0.0);
    }


    /*
     * Prevents `mu` (expected value) from being zero or `m`.
     *
     * Although an observation can technically be zero or `m` it can cause issues (zero division) for the model.
     *
     * @param mu Expected value for an observation.
     * @param m Number of trails for an observation.
     * @return Normalised `mu`.
     */
    private static double muNormalisation(double mu, double m) {
        // compare floats exactly as error only occurs when exactly these values
        if (mu == 0) {
            mu += NORMALISATION_EPSILON;
        } else if (mu == m) {
            mu -= NORMALISATION_EPSILON;
        }

        return mu;
    }


    /*
     * Calculates the number of combinations (binomial coefficient) for the given `total` and `sample` size.
     *
     * @param total Number of object that can be sampled.
     * @param samples Number of object to sample.
     * @return Number of different combinations that can be sampled.
     * @throws ArithmeticException Must satisfy: `0 <= sample <= total`.
     */
    private static long combinations(int total, int samples) throws ArithmeticException {

        if (total < 0 || samples < 0) {
            throw new ArithmeticException("Total and sample size must be non-negative (>= 0).");
        }

        if (samples > total) {
            throw new ArithmeticException("Sample size cannot be larger than total.");
        }

        long result = 1;
        for (int i = 1, j = total; i <= samples; i++, j--) {
            result = (result * j) / i;
        }

        return result;
    }
}
