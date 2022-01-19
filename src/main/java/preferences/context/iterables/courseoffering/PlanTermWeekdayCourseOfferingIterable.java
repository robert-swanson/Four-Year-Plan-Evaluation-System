package preferences.context.iterables.courseoffering;

import objects.misc.TermYear;
import preferences.context.Context;
import preferences.context.TermSubContext;

import java.util.Iterator;

public class PlanTermWeekdayCourseOfferingIterable implements Iterator<TermWeekdayCourseOfferingIterable>, Iterable<TermWeekdayCourseOfferingIterable> {
    private Iterator<TermSubContext> termSubContextIterator;
    private TermYear currentTermYear;

    public PlanTermWeekdayCourseOfferingIterable(Context context) {
        termSubContextIterator = context.getTermSubContexts().values().iterator();
    }

    @Override
    public boolean hasNext() {
        return termSubContextIterator.hasNext();
    }

    @Override
    public TermWeekdayCourseOfferingIterable next() {
        assert hasNext();
        TermSubContext termSubContext = termSubContextIterator.next();
        currentTermYear = termSubContext.getTermYear();
        return termSubContext.termWeekdayCourseOfferingIterable();
    }

    public TermYear getCurrentTermYear() {
        return currentTermYear;
    }

    @Override
    public Iterator<TermWeekdayCourseOfferingIterable> iterator() {
        return this;
    }
}
