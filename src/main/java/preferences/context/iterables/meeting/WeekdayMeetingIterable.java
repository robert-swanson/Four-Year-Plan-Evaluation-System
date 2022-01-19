package preferences.context.iterables.meeting;

import objects.offerings.Meeting;
import preferences.context.WeekdaySubContext;

import java.util.Iterator;

public class WeekdayMeetingIterable implements Iterator<Meeting>, Iterable<Meeting> {
    private final Iterator<Meeting> meetingIterator;

    public WeekdayMeetingIterable(WeekdaySubContext weekdaySubContext) {
        meetingIterator = weekdaySubContext.getMeetings().iterator();
    }

    @Override
    public boolean hasNext() {
        return meetingIterator.hasNext();
    }

    @Override
    public Meeting next() {
        assert hasNext();
        return meetingIterator.next();
    }

    @Override
    public Iterator<Meeting> iterator() {
        return this;
    }
}
