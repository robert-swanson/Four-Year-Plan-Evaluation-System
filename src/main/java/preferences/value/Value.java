package preferences.value;

public abstract class Value {
    public static BooleanValue TRUE = new BooleanValue(true);
    public static BooleanValue FALSE = new BooleanValue(false);

    public boolean isNotNull(){
        return !(this instanceof NullValue);
    }

}
