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

public class GreaterThanOrEqualConstraint extends RequireableConstraint {
    private final ScoreFunction scoreFunction;
    private final ScalableValue minimumValue;

    public GreaterThanOrEqualConstraint(ScalableContextEvaluator scalableContextEvaluator, ScalableValue value, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.greaterThanOrEqual, contextLevel);
        this.minimumValue = value;
        double firstQuartile = value.getScalableValue();
        scoreFunction = new SigmoidScoringFunction(new ScoreBound(ScoreBound.BoundType.soft, firstQuartile - scalableContextEvaluator.getDeviance(contextLevel)), new ScoreBound(ScoreBound.BoundType.soft, firstQuartile));
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
        result.checkResult(value -> minimumValue.compareTo((ScalableValue)value) <= 0, this.toString());
        return result.getCalculatedCheck();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        if (contextEvaluator instanceof NumericContextEvaluator) {
            generate(generator, minimumValue, "or more", contextEvaluator);
        } else if (contextEvaluator instanceof TimeContextEvaluator) {
            generate(generator, contextEvaluator, "at or after", minimumValue);
        } else if (contextEvaluator instanceof TermYearContextEvaluator) {
            generate(generator, contextEvaluator, "on or after", minimumValue);
        }
    }
}
