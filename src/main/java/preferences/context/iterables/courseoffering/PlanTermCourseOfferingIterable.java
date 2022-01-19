package preferences.context.iterables.courseoffering;

import objects.misc.TermYear;
import preferences.context.Context;
import preferences.context.TermSubContext;

import java.util.Iterator;

public class PlanTermCourseOfferingIterable implements Iterator<TermCourseOfferingIterable>, Iterable<TermCourseOfferingIterable> {
    Iterator<TermSubContext> termSubContextIterator;

    public PlanTermCourseOfferingIterable(Context context) {
        termSubContextIterator = context.getTermSubContexts().values().iterator();
    }

    private boolean hasNext = true;
    private TermYear termYear;

    @Override
    public boolean hasNext() {
        return termSubContextIterator.hasNext();
    }

    @Override
    public TermCourseOfferingIterable next() {
        TermSubContext termSubContext = termSubContextIterator.next();
        termYear = termSubContext.getTermYear();
        return termSubContext.termCourseOfferingIterable();
    }

    public TermYear getTermYear() {
        return termYear;
    }

    @Override
    public Iterator<TermCourseOfferingIterable> iterator() {
        return this;
    }
}
