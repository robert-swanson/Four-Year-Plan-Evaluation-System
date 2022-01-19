package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.result.Value;

public class BooleanConstraint extends RequireableConstraint {

    public BooleanConstraint(BooleanContextEvaluator booleanContextEvaluator, ContextLevel contextLevel) {
        super(booleanContextEvaluator, ConstraintType.booleanConstraint, contextLevel);
    }

    public BooleanConstraint(TermYearContextEvaluator termYearContextEvaluator, ContextLevel contextLevel) {
        super(termYearContextEvaluator, ConstraintType.booleanConstraint, contextLevel);
    }

    @Override
    public double score(Context context) {
        Result result = contextEvaluator.getValue(context);
        if (contextEvaluator instanceof  BooleanContextEvaluator) {
            result.scoreResult(value -> ((Value.Boolean)value).value ? 1 : 0, "boolean", this.toString());
        } else {
            result.scoreResult(value -> value == Value.NULL? 0 : 1, "TermYear not empty", this.toString());
        }
        return result.getLastScore();
    }

    @Override
    public boolean fulfilled(Context context) {
        Result result = contextEvaluator.getValue(context);
        if (contextEvaluator instanceof  BooleanContextEvaluator) {
            result.checkResult(value -> ((Value.Boolean)value).value, describe());
        } else {
            result.checkResult(value -> value != Value.NULL, this.toString());
        }
        return result.getCalculatedCheck();
    }

    @Override
    public String describe() {
        return contextEvaluator.toString();
    }

    @Override
    public String toString() {
        return describe();
    }
}
