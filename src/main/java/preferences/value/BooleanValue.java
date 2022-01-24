package preferences.value;

public class BooleanValue extends Value {
    public final boolean value;

    BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value ? "True" : "False";
    }
}
