package preferences.context.iterables.courseoffering;

import objects.offerings.CourseOffering;
import preferences.context.TermSubContext;

import java.util.Iterator;

public class TermCourseOfferingIterable implements Iterator<CourseOffering>, Iterable<CourseOffering> {
    private Iterator<CourseOffering> courseOfferingIterator;

    public TermCourseOfferingIterable(TermSubContext termSubContext) {
        courseOfferingIterator = termSubContext.getCourseOfferings().iterator();
    }

    @Override
    public boolean hasNext() {
        return courseOfferingIterator.hasNext();
    }

    @Override
    public CourseOffering next() {
        assert hasNext();
        return courseOfferingIterator.next();
    }

    @Override
    public Iterator<CourseOffering> iterator() {
        return this;
    }
}
