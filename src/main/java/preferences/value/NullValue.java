package preferences.value;

import psl.PSLGenerator;

public class NullValue extends Value {
    String reason;

    public NullValue(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "Null: " + reason;
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("Null");
    }
}
