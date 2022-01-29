package preferences.evaluators.general;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.Result;
import psl.PSLGenerator;

public class TotalUpperLevelCreditsEvaluator extends NumericContextEvaluator {
    @Override
    public Result getValue(Context context) {
        lastResult = CourseOfferingAccumulatorEvaluator.getValue(context, "Total upper level credits", courseOffering -> courseOffering.getCourse().getNumber() >= 300 ? courseOffering.getCredits() : 0);
        return lastResult;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 5; case terms, days -> 1; };
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 30; case terms -> 8; case days -> 3; };
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("upper division credits");
    }
}
