package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.Result;
import preferences.scoring.OptimumScoringFunction;
import preferences.value.ScalableValue;
import preferences.value.Value;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;

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
    public String describe() {
        return String.format("%s equals %s", contextEvaluator, expectedValue);
    }

    @Override
    public String toString() {
        return describe();
    }

    @Override
    public boolean fulfilled(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.checkResult(expectedValue::equals, this.toString());
        return result.getCalculatedCheck();
    }
}
