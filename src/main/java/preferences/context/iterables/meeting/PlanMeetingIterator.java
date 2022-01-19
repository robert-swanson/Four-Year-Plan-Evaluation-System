package preferences.context.iterables.meeting;

import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.context.TermSubContext;

import java.util.Iterator;

public class PlanMeetingIterator implements Iterator<Meeting>, Iterable<Meeting> {
    private final Iterator<TermSubContext> termSubContextIterator;
    private Iterator<Meeting> termMeetingIterator;

    private boolean hasNext = true;

    public PlanMeetingIterator(Context context) {
        termSubContextIterator = context.getTermSubContexts().values().iterator();
        if (termSubContextIterator.hasNext()) {
            do {
                termMeetingIterator = termSubContextIterator.next().termMeetingIterable().iterator();
            } while (!termMeetingIterator.hasNext() && termSubContextIterator.hasNext());
        }
        if (!termSubContextIterator.hasNext()) {
            hasNext = false;
        }
    }
    @Override
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    public Meeting next() {
        assert  hasNext;
        Meeting rv = termMeetingIterator.next();
        while (!termMeetingIterator.hasNext() && termSubContextIterator.hasNext()) {
            termMeetingIterator = termSubContextIterator.next().termMeetingIterable().iterator();
        }
        if (!termSubContextIterator.hasNext()) {
            hasNext = false;
        }
        return rv;
    }

    @Override
    public Iterator<Meeting> iterator() {
        return this;
    }

}
