package preferences.value;

import psl.PSLGenerator;

public class BooleanValue extends Value {
    public final boolean value;

    BooleanValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return toPSL();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(Boolean.toString(value));
    }
}
