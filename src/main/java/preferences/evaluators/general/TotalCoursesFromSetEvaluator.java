package preferences.evaluators.general;

import objects.misc.CourseID;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.Result;
import psl.PSLGenerator;

import java.util.Set;

public class TotalCoursesFromSetEvaluator extends NumericContextEvaluator {
    Set<CourseID> courseIDSet;
    public TotalCoursesFromSetEvaluator(Set<CourseID> courseSet) {
        courseIDSet = courseSet;
    }

    @Override
    public Result getValue(Context context) {
        CourseOfferingAccumulatorEvaluator.CourseOfferingAttribute courseOfferingAttribute = courseOffering -> courseIDSet.contains(courseOffering.getCourse().getCourseID()) ? 1 : 0;
        lastResult = CourseOfferingAccumulatorEvaluator.getValue(context, String.format("Total courses from set %s", courseIDSet.toString()), courseOfferingAttribute);
        return lastResult;
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
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("courses from %s", CourseID.setToString(courseIDSet)));
    }
}
