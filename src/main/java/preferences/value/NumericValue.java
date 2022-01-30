package preferences.value;

import psl.PSLGenerator;

import java.util.Objects;

public class NumericValue extends ScalableValue {
    private final double value;
    private final transient boolean printAsInt;

    public NumericValue(double v) {
        value = v;
        printAsInt = false;
    }

    public NumericValue(int i) {
        value = i;
        printAsInt = true;
    }

    public double getValue() {
        return value;
    }

    @Override
    public String toString() {
        if (printAsInt) {
            return Integer.toString((int) value);
        } else {
            return String.format("%.2f", value);
        }
    }

    @Override
    public double getScalableValue() {
        return value;
    }

    @Override
    public int compareTo(ScalableValue o) {
        assert o instanceof NumericValue;
        return Double.compare(value, ((NumericValue) o).value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumericValue numeric = (NumericValue) o;
        return Double.compare(numeric.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        if (value == 0) {
            generator.addPSL("no");
        } else if (value == (int)value) {
            generator.addPSL(Integer.toString((int) value));
        } else {
            generator.addPSL(Double.toString(value));
        }
    }
}
