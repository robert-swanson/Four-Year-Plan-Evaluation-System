package preferences.context.iterables.courseoffering;

import preferences.context.TermSubContext;
import preferences.context.WeekSubContext;
import preferences.context.WeekdaySubContext;

import java.util.Iterator;

public class TermWeekdayCourseOfferingIterable implements Iterator<WeekdayCourseOfferingIterable>, Iterable<WeekdayCourseOfferingIterable> {
    private final Iterator<WeekSubContext> weekSubContextIterator;
    private Iterator<WeekdaySubContext> weekdaySubContextIterator;

    private boolean hasNext = true;

    private WeekSubContext currentWeekSubContext;
    private WeekdaySubContext currentWeekdaySubContext;

    public TermWeekdayCourseOfferingIterable(TermSubContext termSubContext) {
        weekSubContextIterator = termSubContext.getWeekSubContexts().iterator();

        if (weekSubContextIterator.hasNext()) {
            do {
                currentWeekSubContext = weekSubContextIterator.next();
                weekdaySubContextIterator = currentWeekSubContext.getWeekdaySubcontexts().values().iterator();
            } while (!weekdaySubContextIterator.hasNext() && weekSubContextIterator.hasNext());
        }
        if (!weekSubContextIterator.hasNext()) {
            hasNext = false;
        }
    }

    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public WeekdayCourseOfferingIterable next() {
        assert hasNext;
        currentWeekdaySubContext =  weekdaySubContextIterator.next();
        WeekdayCourseOfferingIterable rv = currentWeekdaySubContext.courseOfferingIterable();

        while (!weekdaySubContextIterator.hasNext()) {
            if (!weekSubContextIterator.hasNext()) {
                hasNext = false;
                return rv;
            }
            weekdaySubContextIterator = weekSubContextIterator.next().getWeekdaySubcontexts().values().iterator();
        }
        return rv;
    }

    public String getCurrentWeekday() {
        return String.format("%s %s", currentWeekdaySubContext.getWeekday(), currentWeekSubContext);
    }

    @Override
    public Iterator<WeekdayCourseOfferingIterable> iterator() {
        return this;
    }
}
