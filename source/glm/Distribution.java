package glm;


/*
 * The abstract distribution basis of a General Linear Model (GLM) for a fill rank model.
 *
 * For this implementation, a GLM uses a single distribution of an exponential family.
 * Uses Iterative reWeighted Least Squares (IWLS) to find the Maximum Likelihood Estimates (MLE).
 * Can only model full rank models.
 * Can model numerical, factors and interaction of explanatory variables (example below).
 *     A = discrete | B = continuous | C = factor with 3 levels (1, 2, 3)
 *     The matrix below shows A, B and C with interaction with A on B and C.
 *     C = 1 is a base factor.
 *        A,   B, C=2, C=3, A*B, A*(C=2), A*(C=3)
 *     {{10, 1.0,   0,   0,  10,       0,      0},
 *      {20, 1.1,   0,   0,  22,       0,      0},
 *      {30, 1.2,   1,   0,  36,      30,      0},
 *      {40, 1.3,   1,   0,  52,      40,      0},
 *      {50, 1.4,   0,   1,  70,       0,     50},
 *      {60, 1.5,   0,   1,  90,       0,     60},
 *      {70, 1.6,   0,   1, 112,       0,     70}}
 */
abstract class Distribution {

    // values used to round final results when displayed
    private static final int ROUNDING_SI = 3;
    private static final double ROUNDER = Math.pow(10, ROUNDING_SI);

    // minimum change in log likelihood between two iterations of the IWLS to stop
    private static final double LOG_LIKE_EPSILON = 0.0001;

    // required data for any model
    private final Matrix ys;
    private final Matrix xs;

    private Matrix betas;
    private int iterations = 0;


    /*
     * Instantiates a basic abstract distribution for a GLM.
     *
     * @param ys Response variable vector.
     * @param xs Explanatory variable matrix.
     *               Each column is a separate parameter.
     *               Can use continuous, factors and interaction.
     * @throws ArithmeticException Both `ys` and `xs` are required to be in a valid mathematical form.
     *                                 `ys` and `xs` must have the same number of rows.
     *                                 `ys` must be a vector (single column).
     *                                 `xs` cannot have more columns than rows.
     *                                 Model must be full rank (not checked, but will cause errors).
     */
    Distribution(Matrix ys, Matrix xs) throws ArithmeticException {

        if (ys.getNRows() != xs.getNRows()) {
            throw new ArithmeticException("`ys` and `xs` must have the same number of rows.");
        }

        if (ys.getNCols() != 1) {
            throw new ArithmeticException("`ys` must be a vector (single column).");
        }

        if (xs.getNCols() > xs.getNRows()) {
            throw new ArithmeticException("`xs` cannot have more columns than rows.");
        }

        this.ys = ys;
        this.xs = xs;
    }


    /**
     * Gets the string representation of the fitted General Linear Model (GLM).
     *
     * <ul>
     *     <li> String contains the estimated linear predictors and the number of the iterations of Iterative reWeighted
     *          Least Squares (IWLS) to find them.
     * </ul>
     *
     * @return String representation of fitted GLM.
     */
    @Override
    public String toString() {

        Matrix betasRounded = betas.getMap(x -> Math.round(x * ROUNDER) / ROUNDER);
        return "Iterations: " + iterations
                + "\nBeta:\n" + betasRounded.toString();
    }


    /*
     * Estimates `betas` parameters using IWLS.
     *
     * Method should be invoked at the end of an expanding distribution class' constructor.
     * Uses methods implemented in an expanding distribution class.
     */
     void estiBetas() {

        // to prevent being recalculated each iteration
        Matrix xsT = xs.getTranspose();

        // set initial expected values to the true values of `ys`
        Matrix mus = ys;

        double logLikePrev;
        double logLike = logLike(ys, mus);

        // perform IWLS until the log likelihood does not change significantly between iterations (estimates found)
        do {
            Matrix etas = linkFunc(mus);
            Matrix gDiffs = linkFuncDiff(mus);
            Matrix weight = calcWeight(mus, gDiffs);

            Matrix z = calcZ(etas, mus, gDiffs);

            // betaVar = (X^T * W * X)^(-1)
            Matrix betaVar = xsT.getMultiplication(weight).getMultiplication(xs).getInverse();

            // betas = (X^T * W * X)^(-1) * X^T * W * z
            betas = betaVar.getMultiplication(xsT).getMultiplication(weight).getMultiplication(z);

            // find the new expected value using the current `betas`
            etas = xs.getMultiplication(betas);
            mus = linkFuncInv(etas);

            logLikePrev = logLike;
            logLike = logLike(ys, mus);

            iterations++;

        } while (Math.abs(logLike - logLikePrev) > LOG_LIKE_EPSILON);
    }


    /*
     * Calculates the weight matrix.
     *
     * W = 1 / (g'(mu)^2 * v(mu))
     *
     * @param mus Current expected value vector.
     * @param gDiffs Current expected value transformed by the derivative of the link function (g'(mu)).
     * @return Diagonal weight matrix.
     */
    private Matrix calcWeight(Matrix mus, Matrix gDiffs) {
        return gDiffs.getZip(varFunc(mus), (gDiff, var) -> 1 / (Math.pow(gDiff, 2) * var)).getDiagonalisation();
    }


    /*
     * Calculates the z observations for the current estimates.
     *
     * z = etas + g'(mu) * (ys - mus)
     *
     * @param etas Current eta vector.
     *                 eta = g(mu) = X * beta
     * @param mus Current expected value vector.
     * @param gDiffs Current expected value transformed by the derivative of the link function (g'(mu)).
     * @return Z observations vector.
     */
    private Matrix calcZ(Matrix etas, Matrix mus, Matrix gDiffs) {
        Matrix errors = ys.getZip(mus, (y, mu) -> y - mu);
        Matrix scaledErrors = errors.getZip(gDiffs, (gDiff, scale) -> gDiff * scale);
        return etas.getZip(scaledErrors, Double::sum);
    }


    // below are the required functions to be implemented by a distribution for IWLS to work
    abstract Matrix linkFunc(Matrix mus);
    abstract Matrix linkFuncDiff(Matrix mus);
    abstract Matrix linkFuncInv(Matrix etas);
    abstract Matrix varFunc(Matrix mus);
    abstract double logLike(Matrix ys, Matrix mus);
}
