package glm;


/**
 * An immutable generic tuple with 3 values

 * Used for returning multiple values
 */
public class Tuple3 <A, B, C>
{
    private final A item0;
    private final B item1;
    private final C item2;


    /**
     * Instantiates a tuple with 3 values

     * @param item0
         * Value of first value
     * @param item1
         * Value of second value
     * @param item2
         * Value of third value
     */
    public Tuple3(
        final A item0,
        final B item1,
        final C item2)
    {
        this.item0 = item0;
        this.item1 = item1;
        this.item2 = item2;
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


    /**
     * Gets the third value of `this` tuple

     * @returns
         * Third value of `this` tuple
     */
    public C getItem2()
    {
        return item2;
    }
}
