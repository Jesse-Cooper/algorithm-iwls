package glm;


import java.lang.Math;


/**
 * An abstract distribution for General Linear Models (GLMs)

 * Uses Iterative reWeighted Least Squares (IWLS) to find the Maximum Likelihood
   Estimates (MLEs) of the GLM
 * Distribution extensions of this class must be from the exponential family
 * Abstract methods of this class are specific to the extending distribution
   and are used in IWLS
 * Models can be either full rank or less than full rank
 * Models can handle numerical variables, factors and interactions
 */
abstract class Distribution
{
    // * Min change in log-likelihood between two iterations of the IWLS to stop
    private static final double LOG_LIKE_EPSILON = 1e-4;

    // * Required data for any model
    private final Matrix ys;
    private final Matrix xs;

    private final String distributionName;
    private final String linkFuncName;

    private boolean is_fitted = false;

    private Matrix betas;
    private int iterations = 0;
    private double aic;


    /**
     * Instantiates a basic abstract distribution for a GLM

     * @param ys
         * Response vector
     * @param xs
         * Explanatory matrix
     * @param distributionName
         * Name of the extending distribution
     * @param linkFuncName
         * Name of the link function used in the extending distribution
     * @throws ArithmeticException
         * `ys` must be a vector
     * @throws ArithmeticException
         * `ys` must have as many elements as `xs` has rows
     * @throws ArithmeticException
         * `xs` cannot have more columns than rows
     */
    public Distribution(
        final Matrix ys              ,
        final Matrix xs              ,
        final String distributionName,
        final String linkFuncName    )
    throws ArithmeticException
    {
        if (!ys.isVector())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `ys` must be a vector\n"
                + "* `ys` has " + ys.getNCols() + " columns\n"
            );
        }

        if (ys.getNRows() != xs.getNRows())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `ys` must have as many elements as `xs` has rows\n"
                + "* `ys` has " + ys.getNRows() + " elements\n"
                + "* `xs` has " + ys.getNRows() + " rows\n"
            );
        }

        if (xs.getNCols() > xs.getNRows())
        {
            throw new ArithmeticException(
                "\n\n"
                + "* `xs` cannot have more columns than rows\n"
                + "* `xs` is size " + xs.getNRows() + "x" + xs.getNCols() + "\n"
            );
        }

        this.ys = ys;
        this.xs = xs;

        this.distributionName = distributionName;
        this.linkFuncName = linkFuncName;
    }


    /**
     * Gets the string representation `this` model

     * String covers multiple lines
     * Lists in order:
         * Name of model distribution
         * Name of link function
         * Number of IWLS iterations to fit `this` model
         * Fitted model AIC
         * Fitted model parameters

     * @return
         * String representation of `this` model
     */
    @Override
    public final String toString()
    {
        return (
            "Distribution: " + distributionName + "\n"
                + "Link Function: " + linkFuncName + "\n"
                + "IWLS Iterations: " + iterations + "\n"
                + "AIC: " + Numerical.round(aic) + "\n"
                + "Beta:\n" + betas
        );
    }


    /**
     * Estimates model parameters (`betas`) using IWLS

     * Method should be invoked at the end of an expanding distribution class'
       constructor
     * Uses abstract methods implemented in an expanding distribution class
     */
     final void estiBetas()
     {
        final Matrix xsT;
        Matrix mus, etas;
        Matrix gDiffs;
        Matrix ws, zs;
        double logLikePrev, logLike;

        xsT = xs.transpose();

        // * Initial expected value of each observation starts as the true
        //   values of `ys`
        mus = ys;

        // * Perform IWLS until the log-likelihood does not change significantly
        //   between two iterations (estimates found)
        logLike = logLike(ys, mus);
        do
        {
            // * Calculate the current linear predictor of each observation
            // * `etas = g(mus)`
            etas = linkFunc(mus);

            // * Calculate the weights and Z-observations from the current model
            //   parameters encoded in `mus` and `etas`
            // * `ws_i_i = 1 / (g'(mu_i)^2 * v(mu_i))`
            // * `zs = etas + g'(mus) * (ys - mus)`
            gDiffs = linkFuncDiff(mus);
            ws = calcW(mus, gDiffs);
            zs = calcZ(etas, mus, gDiffs);

            // * Find the new model parameters
            // * `betas = (xsT * ws * xs)^+ * xsT * ws * zs`
            betas = xsT.matrixProduct(ws)
                       .matrixProduct(xs)
                       .pseudoinverse()
                       .matrixProduct(xsT)
                       .matrixProduct(ws)
                       .matrixProduct(zs);

            // * Find the new expected value of each observation from the
            //   current `betas`
            // * `etas = xs * betas`
            // * `mus = g^(-1)(etas) = g^(-1)(xs * betas)`
            etas = xs.matrixProduct(betas);
            mus = linkFuncInv(etas);

            logLikePrev = logLike;
            logLike = logLike(ys, mus);

            iterations += 1;
        }
        while (Math.abs(logLike - logLikePrev) > LOG_LIKE_EPSILON);

        is_fitted = true;
        aic = calcAic(logLike);
    }


    /**
     * Calculates the current weight matrix (`ws`) from the current expected
       value of each observation (`mus`) with the link function derivative
       (`g'(mus)`)

     * `ws_i_i = 1 / (g'(mu_i)^2 * v(mu_i))`
     * Weight matrix is a diagonal matrix

     * @param mus
         * Current expected value of each observation
     * @param gDiffs
          * Current value of the link function derivative (`g'(mus)`)
     * @return
          * Current diagonal weight matrix (`ws`)
     */
    private Matrix calcW(
        final Matrix mus   ,
        final Matrix gDiffs)
    {
        final Matrix vars;

        vars = varFunc(mus);
        return gDiffs.zipMatrix(vars, (gDiff, var) -> 1 / (gDiff * gDiff * var))
                     .diagonaliseVec();
    }


    /**
     * Calculates the current Z-observations from the current expected value and
       linear predictor of each observation (`mus` and `etas`) with the link
       function derivative (`g'(mus)`)

     * `zs = etas + g'(mus) * (ys - mus)`

     * @param etas
         * Current linear predictor of each observation
     * @param mus
         * Current expected value of each observation
     * @param gDiffs
         * Current value of the link function derivative (`g'(mus)`)
     * @return
         * Current Z-observations (`zs`)
     */
    private Matrix calcZ(
        final Matrix etas  ,
        final Matrix mus   ,
        final Matrix gDiffs)
    {
        final Matrix errors, errorScales;

        errors = ys.zipMatrix(mus, (y, mu) -> y - mu);
        errorScales = errors.zipMatrix(gDiffs, (gDiff, scale) -> gDiff * scale);
        return etas.zipMatrix(errorScales, Double::sum);
    }


    /**
     * Calculates the Akaike Information Criterion (AIC) for `this` fitted model

     * `AIC = 2 * ([number of coefficients] - logLike)`

     * @param logLike
         * Log-likelihood of `this` fitted model
     * @return
         * AIC of `this` fitted model
     * @throws Error
         * `this` model must be fitted
     */
    private double calcAic(final double logLike) throws Error
    {
        if (!is_fitted)
        {
            throw new Error(
                "\n\n"
                + "* `this` model must be fitted\n"
            );
        }

        return 2 * (betas.getNRows() - logLike);
    }


    // * Required methods to be implemented by a distribution for IWLS to work
    abstract Matrix linkFunc(Matrix mus);
    abstract Matrix linkFuncDiff(Matrix mus);
    abstract Matrix linkFuncInv(Matrix etas);
    abstract Matrix varFunc(Matrix mus);
    abstract double logLike(Matrix ys, Matrix mus);


    /**
     * Calculates the response point estimate of `this` model for the instance
       `ts`

     * @param ts
         * Instance to calculate a response point estimate for
     * @return
         * Response point estimate of `this` model for the instance `ts`
     * @throws Error
         * `this` model must be fitted
     * @throws Error
         * `ts` must be a vector with have as many elements as `this` model has
           coefficients
     */
    public double pointEstimate(final Matrix ts) throws Error
    {
        if (!is_fitted)
        {
            throw new Error(
                "\n\n"
                + "* `this` model must be fitted\n"
            );
        }

        if (!ts.isVector() || ts.getNRows() != betas.getNRows())
        {
            throw new Error(
                "\n\n"
                + "* `ts` must be a vector with have as many elements as "
                    + "`this` model has coefficients\n"
                + "* `this` model has " + betas.getNRows() + " coefficients\n"
                + "* `ts` has " + ts.getNRows() + " elements\n"
            );
        }

        return betas.innerProduct(ts);
    }
}
