package preferences.evaluators.general;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.*;

public class TotalCoursesEvaluator extends CourseOfferingAccumulatorEvaluator {
    @Override
    public Result getValue(Context context) {
        return getValue(context, "Total credits", courseOffering -> 1);
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 5; case terms, days -> 1; };
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 120.0/3.0; case terms -> 4.0; case days -> 2.0; };
    }

    @Override
    public String toString() {
        return "courses";
    }
}
