package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.evaluators.TimeContextEvaluator;
import preferences.result.Result;
import preferences.scoring.SigmoidScoringFunction;
import preferences.value.ScalableValue;
import preferences.scoring.ScoreBound;
import preferences.scoring.ScoreFunction;
import psl.PSLGenerator;

public class LessThanOrEqualConstraint extends RequireableConstraint {
    private final ScoreFunction scoreFunction;
    private final ScalableValue maximumValue;

    public LessThanOrEqualConstraint(ScalableContextEvaluator scalableContextEvaluator, ScalableValue value, ContextLevel contextLevel) {
        super(scalableContextEvaluator, ConstraintType.greaterThanOrEqual, contextLevel);
        this.maximumValue = value;
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
        result.checkResult(value -> maximumValue.compareTo((ScalableValue)value) >= 0, this.toString());
        return result.getCalculatedCheck();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        if (contextEvaluator instanceof NumericContextEvaluator) {
            generate(generator, "no more than", maximumValue, contextEvaluator);
        } else if (contextEvaluator instanceof TimeContextEvaluator) {
            generate(generator, contextEvaluator, "at or before", maximumValue);
        } else if (contextEvaluator instanceof TermYearContextEvaluator) {
            generate(generator, contextEvaluator, "on or before", maximumValue);
        }
    }
}
