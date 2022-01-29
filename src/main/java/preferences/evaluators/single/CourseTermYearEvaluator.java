package preferences.evaluators.single;

import objects.misc.CourseID;
import objects.offerings.CourseOffering;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.context.iterables.courseoffering.PlanCourseOfferingIterator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.value.NullValue;
import preferences.value.TermYearValue;
import preferences.value.Value;
import psl.PSLGenerator;

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
                Value v = new TermYearValue(iterator.getCurrentTermYear());
                String explanation = courseOffering.toString();
                lastResult = new PlanResult<>(v);
                return lastResult;
            }
        }
        lastResult = new PlanResult<>(new NullValue(String.format("Course '%s' isn't scheduled in this context", courseID)));
        return lastResult;
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
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("taking course %s", courseID));
    }
}
