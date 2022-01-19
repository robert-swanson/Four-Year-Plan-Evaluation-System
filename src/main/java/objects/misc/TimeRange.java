package objects.misc;

public class TimeRange implements Comparable<TimeRange> {
    public final Time start, end;

    public TimeRange(Time first, Time second) {
        if (first.isBeforeOrEqual(second)) {
            this.start = first;
            this.end = second;
        } else {
            this.start = second;
            this.end = first;
        }
    }

    @Override
    public int compareTo(TimeRange o) {
        return start.compareTo(o.start);
    }

    public boolean isTimeInRangeExclusive(Time time) {
        return start.compareTo(time) < 0 && time.compareTo(end) < 0;
    }

    public boolean overlapsWith(TimeRange other) {

        return isTimeInRangeExclusive(other.start) || isTimeInRangeExclusive(other.end) || other.isTimeInRangeExclusive(start);
    }

    @Override
    public String toString() {
        return String.format("%s - %s", start, end);
    }
}
