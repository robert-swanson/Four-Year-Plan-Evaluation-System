package preferences.value;

import psl.PSLComponent;

public abstract class Value implements PSLComponent {
    public static BooleanValue TRUE = new BooleanValue(true);
    public static BooleanValue FALSE = new BooleanValue(false);

    public boolean isNotNull(){
        return !(this instanceof NullValue);
    }

    private String explanation;
    public Value setExplanation(String explanation) {
        this.explanation = explanation;
        return this;
    }
    public Value setExplanation(StringBuilder builder) {
        return setExplanation(builder.toString());
    }
}
