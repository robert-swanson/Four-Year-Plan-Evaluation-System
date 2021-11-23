package preferences.evaluators.single;

import objects.misc.TermYear;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.result.Value;

import java.util.Set;

public class FirstTermEvaluator implements TermYearContextEvaluator {
    @Override
    public Result getValue(Context context) {
        TermYear first = null;
        Set<TermYear> terms = context.getTerms();
        String description = "First term in context";
        String explanation = terms.toString();
        for (TermYear termYear : terms) {
            if (first == null || termYear.compareTo(first) < 0) {
                first = termYear;
            }
        }
        if (first == null) {
            return new PlanResult<>(description, explanation, Value.NULL);
        } else {
            return new PlanResult<>(description, explanation, new ScalableValue.TermYearValue(first));
        }
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 0.5;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 2020.0;
    }

    @Override
    public String toString() {
        return "first term";
    }
}
