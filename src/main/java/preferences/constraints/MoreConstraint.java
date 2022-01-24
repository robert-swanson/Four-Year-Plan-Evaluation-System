package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;

public class MoreConstraint extends Constraint {
    private final ScoreFunction scoreFunction;

    public MoreConstraint(ScalableContextEvaluator scalableContextEvaluator, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.more, contextLevel);
        double average = scalableContextEvaluator.getAverage(contextLevel);
        double dev = scalableContextEvaluator.getDeviance(contextLevel);
        scoreFunction = new ScoreFunction.Proportional(new ScoreBound(ScoreBound.BoundType.soft, average-dev/2.0), new ScoreBound(ScoreBound.BoundType.soft, average+dev/2.0));
    }

    @Override
    public String describe() {
        return String.format("more %s", contextEvaluator);
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
}