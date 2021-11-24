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

public class FirstTermEvaluator extends TermYearContextEvaluator {
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
            lastResult = new PlanResult<>(Value.NULL);
        } else {
            lastResult = new PlanResult<>(new ScalableValue.TermYearValue(first));
        }
        return lastResult;
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
    public String describe() {
        return "first term";
    }
}
