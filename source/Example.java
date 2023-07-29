import glm.Binomial;
import glm.Matrix;
import glm.Poisson;


/**
 * An example on how to use the General Linear Model (GLM) package.
 *
 * <ul>
 *     <li> Provides 2 distribution models: Binomial and Poisson.
 *     <li> Applies the same data in different forms to the 2 provided GLM distributions.
 * </ul>
 */
public class Example {

    // data to input to Binomial model
    private static final double[][] YS_BINOMIAL = {{32},
                                                   {25},
                                                   {10}};
    private static final double[][] XS_BINOMIAL = {{1, 1},
                                                   {1, 2},
                                                   {1, 3}};
    private static final double[][] MS_BINOMIAL = {{38},
                                                   {42},
                                                   {20}};

    // data to input to Poisson model
    private static final double[][] YS_POISSON = {{32},
                                                  { 6},
                                                  {25},
                                                  {17},
                                                  {10},
                                                  {10}};
    private static final double[][] XS_POISSON = {{1, 1, 1},
                                                  {1, 2, 1},
                                                  {1, 1, 2},
                                                  {1, 2, 2},
                                                  {1, 1, 3},
                                                  {1, 2, 3}};


    /**
     * Fits 2 models (Binomial and Poisson) and displays their linear estimates.
     *
     * <ul>
     *     <li> Entry point for program.
     * </ul>
     *
     * @param args The program does not use any arguments provided.
     */
    public static void main(String[] args) {

        // fit GLMs for both Binomial and Poisson
        Binomial modelBinomial = new Binomial(new Matrix(YS_BINOMIAL),
                                              new Matrix(XS_BINOMIAL),
                                              new Matrix(MS_BINOMIAL));
        Poisson modelPoisson = new Poisson(new Matrix(YS_POISSON), new Matrix(XS_POISSON));

        // display the fitted models
        System.out.println("Binomial:\n" + modelBinomial);
        System.out.println("\nPoisson:\n" + modelPoisson);
    }
}
