package glm;


/**
 * A generic single value used in lambda calculus

 * Java requires all variables used in lambda functions to be essentially final
 * Variables set to this class can be final while the internal value can change
 */
public class LambdaValue <A>
{
    private A value;


    /**
     * Instantiates a new lambda value

     * @param <A>
         * Type of the stored value
     * @param value
         * Initial value to store
     */
    public LambdaValue(final A value)
    {
        this.value = value;
    }


    /**
     * Gets the stored value

     * @return
         * Stored value
     */
    public A getValue()
    {
        return value;
    }


    /**
     * Sets the stored value

     * @param value
         * Value to store
     */
    public void setValue(final A value)
    {
        this.value = value;
    }
}
