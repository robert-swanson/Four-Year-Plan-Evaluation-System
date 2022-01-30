package preferences.evaluators.general;

import objects.misc.CourseID;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.Result;
import psl.PSLGenerator;

import java.util.Set;

public class TotalCreditsFromSetEvaluator extends NumericContextEvaluator {
    Set<CourseID> courseIDSet;

    public TotalCreditsFromSetEvaluator(Set<CourseID> courseSet) {
        courseIDSet = courseSet;
    }

    @Override
    public Result getValue(Context context) {
        lastResult = CourseOfferingAccumulatorEvaluator.getValue(context, String.format("Total credits from set %s", courseIDSet.toString()), courseOffering -> courseIDSet.contains(courseOffering.getCourse().getCourseID()) ? courseOffering.getCredits() : 0);
        return lastResult;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return courseIDSet.size() * 3.0 / 4.0;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return courseIDSet.size()/2.0;
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("%s from %s", generator.plural ? "credits" : "credit", CourseID.setToString(courseIDSet)));
    }
}
