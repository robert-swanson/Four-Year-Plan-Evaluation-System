package preferences.evaluators.single;

import objects.misc.CourseID;
import objects.offerings.CourseOffering;
import preferences.evaluators.ContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.context.iterables.courseoffering.PlanCourseOfferingIterator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.result.Value;

public class CourseTermYearEvaluator extends TermYearContextEvaluator {
    final private CourseID courseID;
    public CourseTermYearEvaluator(CourseID courseID) {
        this.courseID = courseID;
    }

    @Override
    public Result getValue(Context context) {
        PlanCourseOfferingIterator iterator = context.courseOfferingIterator();
        String description = "The earliest term-year when a course is scheduled";
        for (CourseOffering courseOffering : iterator) {
            if (courseID.equals(courseOffering.getCourse().getCourseID())) {
                Value v = new ScalableValue.TermYearValue(iterator.getCurrentTermYear());
                String explanation = courseOffering.toString();
                return new PlanResult<>(description, explanation, v);
            }
        }
        lastValue = new PlanResult<>(description, "Course is not scheduled", Value.NULL);
        return lastValue;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 2.0;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 2020;
    }

    @Override
    public String describe() {
        return String.format("%s scheduled", courseID);
    }
}
