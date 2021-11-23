package preferences.result;

import objects.misc.TermYear;
import objects.misc.Time;

import java.util.Objects;

public abstract class ScalableValue extends Value implements Comparable<ScalableValue> {
    public abstract double getScalableValue();

    public static class Numeric extends ScalableValue  {
        private final double value;
        private final boolean printAsInt;

        public Numeric(double v) {
            value = v;
            printAsInt = false;
        }

        public Numeric(int i) {
            value = i;
            printAsInt = true;
        }

        public double getValue() {
            return  value;
        }

        @Override
        public String toString() {
            if (printAsInt) {
                return Integer.toString((int)value);
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
            assert o instanceof Numeric;
            return Double.compare(value, ((Numeric) o).value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Numeric numeric = (Numeric) o;
            return Double.compare(numeric.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }
    public static class TimeValue extends ScalableValue {
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
            assert o instanceof TimeValue;
            return value.compareTo(((TimeValue) o).value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeValue timeValue = (TimeValue) o;
            return value.equals(timeValue.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

    public static class TermYearValue extends ScalableValue {
        private final TermYear termYear;
        private final double value;

        public TermYearValue(TermYear termYear) {
            this.termYear = termYear;
            this.value = termYear.year() + switch (termYear.term) {
                case jterm -> 0;
                case spring -> 1/12;
                case summer -> 5/12;
                case fall -> 7/12;
                case winter -> 11/12;
            };
        }

        @Override
        public double getScalableValue() {
            return value;
        }

        @Override
        public String toString() {
            return termYear.toString();
        }

        @Override
        public int compareTo(ScalableValue o) {
            return Double.compare(value, o.getScalableValue());
        }
    }
}
