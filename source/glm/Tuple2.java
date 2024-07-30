package glm;


/**
 * An immutable generic tuple with 2 values

 * Used for returning multiple values
 */
public class Tuple2 <A, B>
{
    private final A item0;
    private final B item1;


    /**
     * Instantiates a tuple with 2 values

     * @param item0
         * Value of first value
     * @param item1
         * Value of second value
     */
    public Tuple2(
        final A item0,
        final B item1)
    {
        this.item0 = item0;
        this.item1 = item1;
    }


    /**
     * Gets the first value of `this` tuple

     * @returns
         * First value of `this` tuple
     */
    public A getItem0()
    {
        return item0;
    }


    /**
     * Gets the second value of `this` tuple

     * @returns
         * Second value of `this` tuple
     */
    public B getItem1()
    {
        return item1;
    }
}
