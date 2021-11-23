package preferences.evaluators.single;

import objects.misc.CourseID;
import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.*;

public abstract class SingleCourseOfferingEvaluator implements ScalableContextEvaluator {
    protected CourseID courseID;

    public interface CourseOfferingValue {
        Value getValue(CourseOffering courseOffering);
    }

    public SingleCourseOfferingEvaluator(CourseID courseID) {
        this.courseID = courseID;
    }

    Result<Value> getValue(Context context, String description, CourseOfferingValue courseOfferingValue) {
        for (CourseOffering courseOffering : context.courseOfferingIterator()) {
            if (courseOffering.getCourse().getCourseID().equals(courseID)) {
                return new PlanResult<>(description, description, courseOfferingValue.getValue(courseOffering));
            }
        }
        return new PlanResult<>(description, String.format("could not find %s in context", courseID), Value.NULL);

    }
}
