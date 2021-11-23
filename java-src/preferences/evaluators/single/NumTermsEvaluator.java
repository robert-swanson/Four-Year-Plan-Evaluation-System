package preferences.evaluators.single;

import objects.misc.TermYear;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.result.ScalableValue;

import java.util.Set;

public class NumTermsEvaluator implements ScalableContextEvaluator {
    @Override
    public Result getValue(Context context) {
        Set<TermYear> terms = context.getTerms();
        return new PlanResult<>("Number of terms in context", terms.toString(), new ScalableValue.Numeric(terms.size()));
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 0.5;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 8;
    }

    @Override
    public String toString() {
        return "num terms";
    }
}
