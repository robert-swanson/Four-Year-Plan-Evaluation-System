package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.evaluators.TimeContextEvaluator;
import preferences.result.Result;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;
import preferences.scoring.SigmoidScoringFunction;
import preferences.value.ScalableValue;
import psl.PSLGenerator;

public class LessConstraint extends Constraint {
    private final ScoreFunction scoreFunction;

    public LessConstraint(ScalableContextEvaluator scalableContextEvaluator, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.more, contextLevel);
        double average = scalableContextEvaluator.getAverage(contextLevel);
        double dev = scalableContextEvaluator.getDeviance(contextLevel);
        scoreFunction = new SigmoidScoringFunction(new ScoreBound(ScoreBound.BoundType.soft, average+dev/2.0), new ScoreBound(ScoreBound.BoundType.soft, average-dev/2.0));
    }

    @Override
    public double score(Context context) {
        Result result = contextEvaluator.getValue(context);
        result.scoreResult(value -> scoreFunction.score(((ScalableValue)value).getScalableValue()), scoreFunction.toString(), this.toString());
        return result.getLastScore();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        if (contextEvaluator instanceof NumericContextEvaluator) {
            generate(generator, "less",  contextEvaluator, null);
        } else if (contextEvaluator instanceof TimeContextEvaluator) {
            generate(generator, contextEvaluator, "earlier", null);
        } else if (contextEvaluator instanceof TermYearContextEvaluator) {
            generate(generator, contextEvaluator, "earlier", null);
        }
    }
}
