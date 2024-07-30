package glm;


import java.lang.Math;


/**
 * Various methods to handle double values
 */
public class Numerical
{
    // * Precision of comparing doubles
    // * Doubles are equal down to `EPSILON` decimal places
    private static final double EPSILON = 1e-4;

    // * Values are rounded to `PRECISION` decimal places
    private static final int PRECISION = 3;
    private static final double ROUNDER = Math.pow(10, PRECISION);


    /**
     * Determines whether `x` is equal to `y` down to a defined precision of
       `EPSILON` decimal places

     * @param x
         * Value to compair to `y`
     * @param y
         * Value to compair to `x`
     * @return
         * Indication whether `x` is equal to `y`
     */
    public static boolean isEqual(
        final double x,
        final double y)
    {
        return y - EPSILON < x && x < y + EPSILON;
    }


    /**
     * Rounds `x` to a defined precision `PRECISION` decimal places

     * @param x
         * Value to round
     * @return
         * Rounded value of `x`
     */
    public static double round(final double x)
    {
        return Math.round(x * ROUNDER) / ROUNDER;
    }
}
