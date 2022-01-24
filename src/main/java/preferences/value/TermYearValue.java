package preferences.value;

import objects.misc.TermYear;

public class TermYearValue extends ScalableValue {
    private final TermYear termYear;
    private final double value;

    public TermYearValue(TermYear termYear) {
        this.termYear = termYear;
        this.value = termYear.year() + switch (termYear.term) {
            case jterm -> 0;
            case spring -> 1 / 12;
            case summer -> 5 / 12;
            case fall -> 7 / 12;
            case winter -> 11 / 12;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        preferences.value.TermYearValue that = (preferences.value.TermYearValue) o;
        return termYear.equals(that.termYear);
    }

    @Override
    public int hashCode() {
        return termYear.hashCode();
    }
}
