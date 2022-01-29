package preferences.evaluators.general;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.Result;
import psl.PSLGenerator;

public class TotalUpperLevelCoursesEvaluator extends NumericContextEvaluator {
    @Override
    public Result getValue(Context context) {
        lastResult = CourseOfferingAccumulatorEvaluator.getValue(context, "Total upper level courses", courseOffering -> courseOffering.getCourse().getNumber() >= 300 ? 1 : 0);
        return lastResult;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 5; case terms, days -> 1; };
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 20.0; case terms, days -> 2; };
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("upper division courses");
    }
}
