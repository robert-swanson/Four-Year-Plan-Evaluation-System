package preferences.value;

public class NullValue extends Value {
    String reason;

    public NullValue(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Null: " + reason;
    }
}
