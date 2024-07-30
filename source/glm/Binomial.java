package glm;


import java.lang.Math;


/**
 * Binomial General Linear Model (GLM)

 * Uses the logistic function as a link function
     * `g(x) = 1 / (1 + e^(-x))`
     * Logistic function is the canonical link for Binomial
     * Models fitted are logistic regression models
 */
public class Binomial extends Distribution
{
    // * Used to prevent zero division errors when using the expected values of
    //   the observations (`mus`)
    private static final double NORMALISATION_EPSILON = 1e-4;

    private static final String DISTRIBUTION_NAME = "Binomial";
    private static final String LINK_FUNC_NAME = "Logistic function";


    // * Binomials require an extra parameter
    // * Number of trails per observation
    // * For a single observation: `m = successes + failures = y + failures`
    private final Matrix ms;


    /**
     * Instantiates and fits a Binomial GLM

     * @param ys
         * Response vector
         * Number of successes per observation
     * @param xs
         * Explanatory matrix
     * @param ms
         * Number of trails per observation
     * @throws ArithmeticException
         * `ms` must be a vector
     * @throws ArithmeticException
         * `ys` and `ms` must have the same number of elements
     */
    public Binomial(
        final Matrix ys,
        final Matrix xs,
        final Matrix ms)
    throws ArithmeticException
    {
        super(ys, xs, DISTRIBUTION_NAME, LINK_FUNC_NAME);

        if (!ms.isVector())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `ms` must be a vector\n"
                + "* `ms` has " + ms.getNCols() + " columns\n"
            );
        }

        if (ys.getNRows() != ms.getNRows())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `ys` and `ms` must have the same number of elements\n"
                + "* `ys` has " + ys.getNRows() + " elements\n"
                + "* `ms` has " + ms.getNRows() + " elements\n"
            );
        }

        this.ms = ms;

        super.estiBetas();
    }


    /**
     * Calculates the current linear predictor of each observation (`etas`) from
       the current expected value of each observation (`mus`) using the link
       function

     * `etas = g(mus) = log_e(mus) - log_e(ms - mus)`
     * Expected values in `mus` are normalised to prevent zero division errors

     * @param mus
         * Current expected value of each observation
     * @return
         * Current linear predictor of each observation (`etas`)
     */
    Matrix linkFunc(Matrix mus)
    {
        mus = mus.zipMatrix(ms, Binomial::muNormalisation);
        return mus.zipMatrix(ms, (mu, m) -> Math.log(mu) - Math.log(m - mu));
    }


    /**
     * Calculates the current link function derivative from the current expected
       value of each observation (`mus`)

     * `g'(mus) = ms / (mus * (ms - mus))`
     * Expected values in `mus` are normalised to prevent zero division errors

     * @param mus
         * Current expected value of each observation
     * @return
         * Current value of the link function derivative
     */
    Matrix linkFuncDiff(Matrix mus)
    {
        mus = mus.zipMatrix(ms, Binomial::muNormalisation);
        return mus.zipMatrix(ms, (mu, m) -> m / (mu * (m - mu)));
    }


    /**
     * Calculates the current expected value of each observation (`mus`) from
       the current linear predictor of each observation (`etas`) using the link
       function inverse

     * `mus = g^(-1)(etas) = ms / (1 + e^(-etas))`

     * @param etas
         * Current linear predictor of each observation
         * `etas = xs * betas`
     * @return
         * Current expected value of each observation (`mus`)
     */
    Matrix linkFuncInv(final Matrix etas)
    {
        return etas.zipMatrix(ms, (eta, m) -> m / (1 + Math.exp(-eta)));
    }


    /**
     * Calculates the current variance function from the current expected value
       of each observation (`mus`)

     * Variance function is the effect of an expected value on its observation's
       variance
     * `v(mus) = b''(b'^(-1)(mus)) = mus * (1 - mus / ms)`
     * Expected values in `mus` are normalised to prevent zero division errors

     * @param mus
         * Current expected value of each observation
     * @return
         * Current value of the variance function
     */
    Matrix varFunc(Matrix mus)
    {
        mus = mus.zipMatrix(ms, Binomial::muNormalisation);
        return mus.zipMatrix(ms, (mu, m) -> mu * (1 - mu / m));
    }


    /**
     * Calculates the current log-likelihood of the current model parameters

     * `logLike = sum(ys * log_e(ps) + (ms - ys) * log_e(qs) + C)`
         * `ps = mus / ms`
         * `qs = 1 - ps`
         * `C = log_e(C(ms, ys))`
     * Constant term (`C`) is included even though it cancels out when comparing
       log-likelihoods

     * @param ys
         * Response vector
         * Number of successes per observation
     * @param mus
         * Current expected value of each observation
     * @return
         * Current log-likelihood
     */
    double logLike(
        final Matrix ys ,
        final Matrix mus)
    {
        final Matrix ps, pLogs, qLogs;
        final Matrix qCounts;
        final Matrix pLogProbs, qLogProbs, combinationLogs;
        Matrix logProbs;

        // * Calculate the log probabilities of success (p) and failure (q)
        ps = mus.zipMatrix(ms, (mu, m) -> mu / m);
        pLogs = ps.mapMatrix(Math::log);
        qLogs = ps.mapMatrix(p -> Math.log(1 - p));

        // * Calculate the number of failures per observation
        // * `ys` is the number of success per observation
        qCounts = ms.zipMatrix(ys, (m, y) -> m - y);

        // * Calculate components of the log-likelihood
        pLogProbs = ys.zipMatrix(pLogs, (y, pLog) -> y * pLog);
        qLogProbs = qCounts.zipMatrix(qLogs, (qCount, qLog) -> qCount * qLog);
        combinationLogs = ms.zipMatrix(ys, Binomial::logCombinations);

        // * Sum all log-likelihood components above together
        logProbs = pLogProbs.zipMatrix(qLogProbs, Double::sum);
        logProbs = logProbs.zipMatrix(combinationLogs, Double::sum);

        // * Sum the log probabilities for each observation
        return logProbs.foldVec(Double::sum, 0.0);
    }


    /**
     * Prevents the current expected value of an observation (`mu`) from being
       `0` or `m`

     * Although an expected value can technically be `0` or `m` it can cause
       zero division errors

     * @param mu
         * Current expected value of an observation
     * @param m
         * Number of trails for an observation
     * @return
         * Normalised `mu`
     */
    private static double muNormalisation(
              double mu,
        final double m )
    {
        // * Compare floats exactly as the error only occurs when exactly `0`
        //   or `m`
        if (mu == 0)
        {
            mu += NORMALISATION_EPSILON;
        }
        else if (mu == m)
        {
            mu -= NORMALISATION_EPSILON;
        }

        return mu;
    }


    /**
     * Calculates the log_e of the number of different combinations (binomial
       coefficient)

     * @param total
         * Number of objects that can be sampled
     * @param samples
         * Number of objects to sample
     * @return
         * Log_e of the number of different combinations that can be sampled
     * @throws ArithmeticException
         * `total` and `samples` must be non-negative
         * `total > 0`
         * `samples > 0`
     * @throws ArithmeticException
         * `samples` cannot be larger than `total`
         * `samples > total`
     */
    private static double logCombinations(
        final double total  ,
        final double samples)
    throws ArithmeticException
    {
        int i, j;
        long result;

        if (total < 0 || samples < 0)
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `total` and `samples` must be non-negative\n"
                + "* `total = " + total + "`\n"
                + "* `samples = " + samples + "`\n"
            );
        }

        if (samples > total)
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `samples` cannot be larger than `total`\n"
                + "* `total = " + total + "`\n"
                + "* `samples = " + samples + "`\n"
            );
        }

        result = 1;
        for (i = 1, j = (int) total; i <= samples; i += 1, j -= 1)
        {
            result = (result * j) / i;
        }

        return Math.log(result);
    }
}
