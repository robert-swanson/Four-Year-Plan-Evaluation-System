package preferences.context.iterables.meeting;

import objects.misc.TermYear;
import preferences.context.Context;
import preferences.context.TermSubContext;

import java.util.Iterator;

public class PlanTermMeetingIterable implements Iterator<TermMeetingIterable>, Iterable<TermMeetingIterable> {
    Iterator<TermSubContext> termSubContextIterator;

    public PlanTermMeetingIterable(Context context) {
        termSubContextIterator = context.getTermSubContexts().values().iterator();
    }

    private TermYear termYear;

    @Override
    public boolean hasNext() {
        return termSubContextIterator.hasNext();
    }

    @Override
    public TermMeetingIterable next() {
        TermSubContext termSubContext = termSubContextIterator.next();
        termYear = termSubContext.getTermYear();
        return termSubContext.termMeetingIterable();
    }

    public TermYear getTermYear() {
        return termYear;
    }

    @Override
    public Iterator<TermMeetingIterable> iterator() {
        return this;
    }
}
