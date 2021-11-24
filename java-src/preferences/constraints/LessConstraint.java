package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;

public class LessConstraint extends Constraint {
    private final ScoreFunction scoreFunction;

    public LessConstraint(ScalableContextEvaluator scalableContextEvaluator, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.more, contextLevel);
        double average = scalableContextEvaluator.getAverage(contextLevel);
        double dev = scalableContextEvaluator.getDeviance(contextLevel);
        scoreFunction = new ScoreFunction.Proportional(new ScoreBound(ScoreBound.BoundType.soft, average+dev/2.0), new ScoreBound(ScoreBound.BoundType.soft, average-dev/2.0));
    }

    @Override
    public ConstraintResultExplanation explainLastResult() {
        // TODO: Add evaluation explanation
        return super.explainLastResult();
    }

    @Override
    public String describe() {
        return String.format("less %s", contextEvaluator);
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
}
