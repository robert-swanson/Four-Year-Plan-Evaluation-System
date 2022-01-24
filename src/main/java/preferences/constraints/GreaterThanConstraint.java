package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;

public class GreaterThanConstraint extends RequireableConstraint {
    private final ScoreFunction scoreFunction;
    private final ScalableValue minimumValue;

    public GreaterThanConstraint(ScalableContextEvaluator scalableContextEvaluator, ScalableValue value, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.greaterThan, contextLevel);
        this.minimumValue = value;
        double firstQuartile = value.getScalableValue();
        scoreFunction = new ScoreFunction.Proportional(new ScoreBound(ScoreBound.BoundType.soft, firstQuartile), new ScoreBound(ScoreBound.BoundType.soft, firstQuartile + scalableContextEvaluator.getDeviance(contextLevel)));
    }

    @Override
    public String describe() {
        return String.format("%s greater than %s", contextEvaluator, minimumValue);
    }

    @Override
    public String toString() {
        return describe();
    }

    @Override
    public double score(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.scoreResult(value -> scoreFunction.score(((ScalableValue)value).getScalableValue()), scoreFunction.toString(), this.toString());
        return result.getLastScore();
    }

    @Override
    public boolean fulfilled(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.checkResult(value -> minimumValue.compareTo((ScalableValue)value) < 0, this.toString());
        return result.getCalculatedCheck();
    }
}