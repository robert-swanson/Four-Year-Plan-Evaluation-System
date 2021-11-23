package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;

public class LessThanOrEqualConstraint extends RequireableConstraint {
    private final ScoreFunction scoreFunction;
    private final ScalableValue minimumValue;

    public LessThanOrEqualConstraint(ScalableContextEvaluator scalableContextEvaluator, ScalableValue value, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.greaterThanOrEqual, contextLevel);
        this.minimumValue = value;
        double firstQuartile = value.getScalableValue();
        scoreFunction = new ScoreFunction.Proportional(new ScoreBound(ScoreBound.BoundType.soft, firstQuartile - scalableContextEvaluator.getDeviance(contextLevel)), new ScoreBound(ScoreBound.BoundType.soft, firstQuartile));
    }

    @Override
    public ConstraintResultExplanation explainLastResult() {
        return new ConstraintResultExplanation(this);
        // TODO: Add evaluation explanation
    }

    @Override
    public String describe() {
        return String.format("%s <= %s", contextEvaluator, minimumValue);
    }

    @Override
    public String toString() {
        return describe();
    }

    @Override
    public Result score(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.scoreResult(value -> scoreFunction.score(((ScalableValue)value).getScalableValue()), scoreFunction.toString(), this.toString());
        return result;
    }

    @Override
    public Result fulfilled(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.checkResult(value -> minimumValue.compareTo((ScalableValue)value) >= 0, this.toString());
        return result;
    }
}
