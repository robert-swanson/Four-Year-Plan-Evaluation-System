package preferences.evaluators.single;

import objects.misc.CourseID;
import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.evaluators.ContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.*;

public abstract class SingleCourseOfferingEvaluator extends ContextEvaluator {
    public interface CourseOfferingValue {
        Value getValue(CourseOffering courseOffering);
    }

    static Result<Value> getValue(Context context, String description, CourseOfferingValue courseOfferingValue, CourseID courseID) {
        for (CourseOffering courseOffering : context.courseOfferingIterator()) {
            if (courseOffering.getCourse().getCourseID().equals(courseID)) {
                return new PlanResult<>(courseOfferingValue.getValue(courseOffering));
            }
        }
        return new PlanResult<>(Value.NULL);
    }
}
