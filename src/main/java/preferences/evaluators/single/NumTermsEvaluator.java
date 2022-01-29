package preferences.evaluators.single;

import objects.misc.TermYear;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.value.NumericValue;
import psl.PSLGenerator;

import java.util.Set;

public class NumTermsEvaluator extends NumericContextEvaluator {
    @Override
    public Result getValue(Context context) {
        Set<TermYear> terms = context.getTerms();
        lastResult = new PlanResult<>(new NumericValue(terms.size()));
        return lastResult;
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
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("terms");
    }
}
