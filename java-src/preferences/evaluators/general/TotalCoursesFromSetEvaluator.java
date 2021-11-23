package preferences.evaluators.general;

import objects.misc.CourseID;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.*;

import java.util.Set;

public class TotalCoursesFromSetEvaluator extends CourseOfferingAccumulatorEvaluator {
    Set<CourseID> courseIDSet;

    public TotalCoursesFromSetEvaluator(Set<CourseID> courseSet) {
        courseIDSet = courseSet;
    }

    @Override
    public Result getValue(Context context) {
        CourseOfferingAttribute courseOfferingAttribute = courseOffering -> courseIDSet.contains(courseOffering.getCourse().getCourseID()) ? 1 : 0;
        return getValue(context, String.format("Total courses from set %s", courseIDSet.toString()), courseOfferingAttribute);
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return courseIDSet.size() / 4.0;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return courseIDSet.size()/2.0;
    }

    @Override
    public String toString() {
        return String.format("courses from %s", courseIDSet.toString());
    }
}
