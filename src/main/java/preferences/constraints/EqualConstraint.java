package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.evaluators.TimeContextEvaluator;
import preferences.result.Result;
import preferences.scoring.OptimumScoringFunction;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;
import preferences.value.*;
import psl.PSLGenerator;

public class EqualConstraint extends RequireableConstraint {
    private final ScoreFunction scoreFunction;
    private final Value expectedValue;

    public EqualConstraint(ScalableContextEvaluator scalableContextEvaluator, Value value, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.equal, contextLevel);
        this.expectedValue = value;
        double middle = ((ScalableValue)value).getScalableValue();
        scoreFunction = new OptimumScoringFunction(middle, new ScoreBound(ScoreBound.BoundType.soft, scalableContextEvaluator.getDeviance(contextLevel)));
    }

    @Override
    public double score(Context context) {
        Result result = contextEvaluator.getValue(context);
        if (expectedValue instanceof ScalableValue) {
            result.scoreResult(value -> scoreFunction.score(((ScalableValue)value).getScalableValue()), scoreFunction.toString(), this.toString());
        } else {
            result.scoreResult(value -> scoreFunction.score(value.equals(expectedValue) ? 1.0 : 0.0), scoreFunction.toString(), this.toString());
        }
        return result.getLastScore();
    }

    @Override
    public boolean fulfilled(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.checkResult(expectedValue::equals, this.toString());
        return result.getCalculatedCheck();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        if (contextEvaluator instanceof NumericContextEvaluator) {
            generate(generator, "", expectedValue, contextEvaluator);
        } else if (contextEvaluator instanceof TimeContextEvaluator) {
            generate(generator, contextEvaluator, "at", expectedValue);
        } else if (contextEvaluator instanceof TermYearContextEvaluator) {
            generate(generator, contextEvaluator, "in", expectedValue);
        }
    }
}
