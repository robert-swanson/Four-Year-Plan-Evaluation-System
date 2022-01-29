package preferences.evaluators.single;

import objects.misc.CourseID;
import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.value.NullValue;
import preferences.value.Value;

public abstract class SingleCourseOfferingEvaluator extends TermYearContextEvaluator {
    public interface CourseOfferingValue {
        Value getValue(CourseOffering courseOffering);
    }

    static Result<Value> getValue(Context context, String description, CourseOfferingValue courseOfferingValue, CourseID courseID) {
        for (CourseOffering courseOffering : context.courseOfferingIterator()) {
            if (courseOffering.getCourse().getCourseID().equals(courseID)) {
                return new PlanResult<>(courseOfferingValue.getValue(courseOffering));
            }
        }
        return new PlanResult<>(new NullValue(String.format("Course '%s' isn't scheduled in this context", courseID)));
    }
}
