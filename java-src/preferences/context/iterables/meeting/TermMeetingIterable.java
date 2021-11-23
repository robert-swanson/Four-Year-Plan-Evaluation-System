package preferences.context.iterables.meeting;

import objects.offerings.Meeting;
import preferences.context.TermSubContext;
import preferences.context.WeekSubContext;
import preferences.context.WeekdaySubContext;

import java.util.Iterator;

public class TermMeetingIterable implements Iterator<Meeting>, Iterable<Meeting> {
    private final Iterator<WeekSubContext> weekSubContextIterator;
    private Iterator<WeekdaySubContext> weekdaySubContextIterator;
    private Iterator<Meeting> weekdayMeetingIterator;

    private boolean hasNext = true;

    public TermMeetingIterable(TermSubContext termSubContext) {
        weekSubContextIterator = termSubContext.getWeekSubContexts().iterator();

        if (weekSubContextIterator.hasNext()) {
            do {
                weekdaySubContextIterator = weekSubContextIterator.next().getWeekdaySubcontexts().values().iterator();

                if (weekdaySubContextIterator.hasNext()) {
                    do {
                        weekdayMeetingIterator = weekdaySubContextIterator.next().meetingIterable();
                    } while (!weekdayMeetingIterator.hasNext() && weekSubContextIterator.hasNext());
                }
            } while (!weekdaySubContextIterator.hasNext() && weekSubContextIterator.hasNext());
        }
        if (!weekdayMeetingIterator.hasNext()) {
            hasNext = false;
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Meeting next() {
        assert hasNext;
        Meeting rv = weekdayMeetingIterator.next();

        if (!weekdayMeetingIterator.hasNext()) {
            do {
                if (weekdaySubContextIterator.hasNext()) {
                    do {
                        weekdayMeetingIterator = weekdaySubContextIterator.next().meetingIterable();
                    } while (!weekdayMeetingIterator.hasNext() && weekdaySubContextIterator.hasNext());
                }
                if (!weekdaySubContextIterator.hasNext()) {
                    if (weekSubContextIterator.hasNext()) {
                        weekdaySubContextIterator = weekSubContextIterator.next().getWeekdaySubcontexts().values().iterator();
                    } else { // all three iterators expired
                        hasNext = false;
                        return rv;
                    }
                }
            } while (!weekdaySubContextIterator.hasNext());
        }
        return rv;
    }

    @Override
    public Iterator<Meeting> iterator() {
        return this;
    }
}
