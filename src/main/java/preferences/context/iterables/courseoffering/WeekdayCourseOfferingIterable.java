package preferences.context.iterables.courseoffering;

import objects.offerings.CourseOffering;
import objects.offerings.Meeting;
import preferences.context.WeekdaySubContext;

import java.util.Iterator;

public class WeekdayCourseOfferingIterable implements Iterator<CourseOffering>, Iterable<CourseOffering> {
    private final Iterator<Meeting> meetingIterator;

    public WeekdayCourseOfferingIterable(WeekdaySubContext weekdaySubContext) {
        meetingIterator = weekdaySubContext.getMeetings().iterator();
    }

    @Override
    public boolean hasNext() {
        return meetingIterator.hasNext();
    }

    @Override
    public CourseOffering next() {
        assert hasNext();
        return meetingIterator.next().getCourseOffering();
    }

    @Override
    public Iterator<CourseOffering> iterator() {
        return this;
    }
}
