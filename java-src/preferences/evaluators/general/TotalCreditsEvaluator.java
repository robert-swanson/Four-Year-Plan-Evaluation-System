package preferences.evaluators.general;

import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.*;

public class TotalCreditsEvaluator extends CourseOfferingAccumulatorEvaluator {
    @Override
    public Result getValue(Context context) {
        lastValue = getValue(context, "Total credits", CourseOffering::getCredits);
        return lastValue;
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
    public String describe() {
        return "credits";
    }
}
