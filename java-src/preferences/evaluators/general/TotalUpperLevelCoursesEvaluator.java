package preferences.evaluators.general;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.Result;

public class TotalUpperLevelCoursesEvaluator extends CourseOfferingAccumulatorEvaluator {
    @Override
    public Result getValue(Context context) {
        lastValue = getValue(context, "Total upper level courses", courseOffering -> courseOffering.getCourse().getNumber() >= 300 ? 1 : 0);
        return lastValue;
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
    public String describe() {
        return "upper level courses";
    }
}
