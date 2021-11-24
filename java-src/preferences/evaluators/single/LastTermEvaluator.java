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

public class LastTermEvaluator extends TermYearContextEvaluator {
    @Override
    public Result getValue(Context context) {
        TermYear last = null;
        Set<TermYear> terms = context.getTerms();
        String description = "Last term in context";
        String explanation = terms.toString();
        for (TermYear termYear : terms) {
            if (last == null || termYear.compareTo(last) > 0) {
                last = termYear;
            }
        }
        if (last == null) {
            lastResult = new PlanResult<>(Value.NULL);
        } else {
            lastResult = new PlanResult<>(new ScalableValue.TermYearValue(last));
        }
        return lastResult;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 0.5;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 2020;
    }

    @Override
    public String describe() {
        return "last term";
    }
}
