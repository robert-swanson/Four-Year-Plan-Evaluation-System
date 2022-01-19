package preferences.context.iterables.courseoffering;

import objects.misc.TermYear;
import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.context.TermSubContext;

import java.util.Iterator;

public class PlanCourseOfferingIterator implements Iterator<CourseOffering>, Iterable<CourseOffering> {
    private final Iterator<TermSubContext> termSubContextIterator;
    private Iterator<CourseOffering> termCourseOfferingIterator;

    private boolean hasNext = true;
    private TermYear currentTermYear;

    public PlanCourseOfferingIterator(Context context) {
        termSubContextIterator = context.getTermSubContexts().values().iterator();
        if (termSubContextIterator.hasNext()) {
            do {
                TermSubContext termSubContext = termSubContextIterator.next();
                termCourseOfferingIterator = termSubContext.termCourseOfferingIterable().iterator();
                currentTermYear = termSubContext.getTermYear();
            } while (!termCourseOfferingIterator.hasNext() && termSubContextIterator.hasNext());
        }
        if (termCourseOfferingIterator == null || !termCourseOfferingIterator.hasNext()) {
            hasNext = false;
        }
    }
    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public CourseOffering next() {
        assert  hasNext;
        CourseOffering rv = termCourseOfferingIterator.next();
        while (!termCourseOfferingIterator.hasNext() && termSubContextIterator.hasNext()) {
            TermSubContext termSubContext = termSubContextIterator.next();
            termCourseOfferingIterator = termSubContext.termCourseOfferingIterable().iterator();
            currentTermYear = termSubContext.getTermYear();
        }
        if (!termCourseOfferingIterator.hasNext()) {
            hasNext = false;
        }
        return rv;
    }

    public TermYear getCurrentTermYear() {
        return currentTermYear;
    }

    @Override
    public Iterator<CourseOffering> iterator() {
        return this;
    }
}
