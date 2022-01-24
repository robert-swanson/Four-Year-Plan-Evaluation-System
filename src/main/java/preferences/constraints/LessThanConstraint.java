package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.Result;
import preferences.scoring.SigmoidScoringFunction;
import preferences.value.ScalableValue;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;

public class LessThanConstraint extends RequireableConstraint {
    private final ScoreFunction scoreFunction;
    private final ScalableValue maximumValue;

    public LessThanConstraint(ScalableContextEvaluator scalableContextEvaluator, ScalableValue value, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.lessThan, contextLevel);
        this.maximumValue = value;
        double firstQuartile = value.getScalableValue();
        scoreFunction = new SigmoidScoringFunction(new ScoreBound(ScoreBound.BoundType.soft, firstQuartile), new ScoreBound(ScoreBound.BoundType.soft, firstQuartile - scalableContextEvaluator.getDeviance(contextLevel)));
    }

    @Override
    public String describe() {
        return String.format("%s less than %s", contextEvaluator, maximumValue);
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
        result.checkResult(value -> maximumValue.compareTo((ScalableValue)value) > 0, this.toString());
        return result.getCalculatedCheck();
    }
}
