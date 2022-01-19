package preferences.context.iterables.meeting;

import objects.misc.TermYear;
import preferences.context.Context;
import preferences.context.TermSubContext;

import java.util.Iterator;

public class PlanTermWeekdayMeetingIterable implements Iterator<TermWeekdayMeetingIterable>, Iterable<TermWeekdayMeetingIterable> {
    private Iterator<TermSubContext> termSubContextIterator;
    private TermYear termYear;

    public PlanTermWeekdayMeetingIterable(Context context) {
        termSubContextIterator = context.getTermSubContexts().values().iterator();
    }

    @Override
    public boolean hasNext() {
        return termSubContextIterator.hasNext();
    }

    public TermYear getTermYear() {
        return termYear;
    }

    @Override
    public TermWeekdayMeetingIterable next() {
        assert hasNext();
        TermSubContext termSubContext = termSubContextIterator.next();
        termYear = termSubContext.getTermYear();
        return termSubContext.termWeekdayMeetingIterable();
    }

    @Override
    public Iterator<TermWeekdayMeetingIterable> iterator() {
        return this;
    }
}
