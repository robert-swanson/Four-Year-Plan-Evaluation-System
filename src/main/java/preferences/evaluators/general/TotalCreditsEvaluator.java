package preferences.evaluators.general;

import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.Result;
import psl.PSLGenerator;

public class TotalCreditsEvaluator extends NumericContextEvaluator {
    @Override
    public Result getValue(Context context) {
        lastResult = CourseOfferingAccumulatorEvaluator.getValue(context, "Total credits", CourseOffering::getCredits);
        return lastResult;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 20; case terms, days -> 1; };
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 120; case terms -> 15; case days -> 9; };
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("credits");
    }
}
