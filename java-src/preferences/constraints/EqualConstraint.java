package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.result.Value;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;

public class EqualConstraint extends RequireableConstraint {
    private final ScoreFunction scoreFunction;
    private final Value expectedValue;

    public EqualConstraint(ContextEvaluator scalableContextEvaluator, Value value, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.equal, contextLevel);
        this.expectedValue = value;

        if (value instanceof ScalableValue) {
            double middle = ((ScalableValue)value).getScalableValue();
            scoreFunction = new ScoreFunction.Optimum(middle, new ScoreBound(ScoreBound.BoundType.soft, scalableContextEvaluator.getDeviance(contextLevel)));
        } else {
            scoreFunction = new ScoreFunction.Boolean();
        }
    }

    @Override
    public Result score(Context context) {
        Result result = contextEvaluator.getValue(context);
        if (expectedValue instanceof ScalableValue) {
            result.scoreResult(value -> scoreFunction.score(((ScalableValue)value).getScalableValue()), scoreFunction.toString(), this.toString());
        } else {
            result.scoreResult(value -> scoreFunction.score(value.equals(expectedValue) ? 1.0 : 0.0), scoreFunction.toString(), this.toString());
        }
        return result;
    }

    @Override
    public ConstraintResultExplanation explainLastResult() {
        // TODO: Add evaluation explanation
        return super.explainLastResult();
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
    public Result fulfilled(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.checkResult(expectedValue::equals, this.toString());
        return result;
    }
}
