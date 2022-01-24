package preferences.value;

import objects.misc.Time;

import java.util.Objects;

public class TimeValue extends ScalableValue {
    private final Time value;

    public TimeValue(Time t) {
        value = t;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public double getScalableValue() {
        return value.minutesSinceMidnight();
    }

    @Override
    public int compareTo(ScalableValue o) {
        assert o instanceof preferences.value.TimeValue;
        return value.compareTo(((preferences.value.TimeValue) o).value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        preferences.value.TimeValue timeValue = (preferences.value.TimeValue) o;
        return value.equals(timeValue.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
