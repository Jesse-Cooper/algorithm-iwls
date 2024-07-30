import glm.Binomial;
import glm.Matrix;
import glm.Poisson;


/**
 * An example of how to use the General Linear Model (GLM) package by applying
   the same data in different formats to both a Binomial and Poisson
   distribution
 */
public class Example
{
    // * Data input to the Binomial model
    private static final double[][] YS_BINOMIAL = {
        {32},
        {25},
        {10}
    };
    private static final double[][] XS_BINOMIAL = {
        {1, 1},
        {1, 2},
        {1, 3}
    };
    private static final double[][] MS_BINOMIAL = {
        {38},
        {42},
        {20}
    };

    // * Data input to the Poisson model
    private static final double[][] YS_POISSON = {
        {32},
        { 6},
        {25},
        {17},
        {10},
        {10}
    };
    private static final double[][] XS_POISSON = {
        {1, 1, 1},
        {1, 2, 1},
        {1, 1, 2},
        {1, 2, 2},
        {1, 1, 3},
        {1, 2, 3}
    };


    /**
     * Fits 2 models (Binomial and Poisson) and displays their coefficients

     * @param args
         * This program does not use any arguments provided
     */
    public static void main(String[] args)
    {
        final Matrix ysBinomial, xsBinomial, msBinomial;
        final Matrix ysPoisson, xsPoisson;
        final Binomial modelBinomial;
        final Poisson modelPoisson;

        ysBinomial = new Matrix(YS_BINOMIAL);
        xsBinomial = new Matrix(XS_BINOMIAL);
        msBinomial = new Matrix(MS_BINOMIAL);

        ysPoisson = new Matrix(YS_POISSON);
        xsPoisson = new Matrix(XS_POISSON);

        // * Fit GLMs for both Binomial and Poisson
        modelBinomial = new Binomial(ysBinomial, xsBinomial, msBinomial);
        modelPoisson = new Poisson(ysPoisson, xsPoisson);

        // * Display the fitted models
        System.out.print("\n" + modelBinomial);
        System.out.print("\n" + modelPoisson + "\n");
    }
}
