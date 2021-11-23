package preferences.context.iterables.meeting;

import objects.misc.Weekday;
import preferences.context.TermSubContext;
import preferences.context.WeekSubContext;
import preferences.context.WeekdaySubContext;

import java.util.Iterator;

public class TermWeekdayMeetingIterable implements Iterator<WeekdayMeetingIterable>, Iterable<WeekdayMeetingIterable> {
    private final Iterator<WeekSubContext> weekSubContextIterator;
    private Iterator<WeekdaySubContext> weekdaySubContextIterator;

    private boolean hasNext = true;

    private double currentWeight;
    private WeekSubContext currentWeekSubContext;
    private WeekdaySubContext currentWeekdaySubContext;

    public TermWeekdayMeetingIterable(TermSubContext termSubContext) {
        weekSubContextIterator = termSubContext.getWeekSubContexts().iterator();

        if (weekSubContextIterator.hasNext()) {
            do {
                currentWeekSubContext = weekSubContextIterator.next();
                currentWeight = currentWeekSubContext.getWeight();
                weekdaySubContextIterator = currentWeekSubContext.getWeekdaySubcontexts().values().iterator();
            } while (!weekdaySubContextIterator.hasNext() && weekSubContextIterator.hasNext());
        }
        if (!weekdaySubContextIterator.hasNext()) {
            hasNext = false;
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public WeekdayMeetingIterable next() {
        assert hasNext;
        currentWeekdaySubContext = weekdaySubContextIterator.next();
        WeekdayMeetingIterable rv = currentWeekdaySubContext.meetingIterable();

        while (!weekdaySubContextIterator.hasNext()) {
            if (!weekSubContextIterator.hasNext()) {
                hasNext = false;
                return rv;
            }
            currentWeekSubContext = weekSubContextIterator.next();
            currentWeight = currentWeekSubContext.getWeight();
            weekdaySubContextIterator = currentWeekSubContext.getWeekdaySubcontexts().values().iterator();
        }
        return rv;
    }

    public Weekday getCurrentWeekday() {
        return currentWeekdaySubContext.getWeekday();
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    @Override
    public Iterator<WeekdayMeetingIterable> iterator() {
        return this;
    }

    public WeekSubContext getCurrentWeekSubContext() {
        return currentWeekSubContext;
    }
}
