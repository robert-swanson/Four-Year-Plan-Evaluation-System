package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.result.Result;
import preferences.value.BooleanValue;
import preferences.value.Value;

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
            result.scoreResult(value -> ((BooleanValue)value).value ? 1 : 0, "boolean", this.toString());
        } else {
            result.scoreResult(value -> value.isNotNull()? 1 : 0, "TermYear not empty", this.toString());
        }
        return result.getLastScore();
    }

    @Override
    public boolean fulfilled(Context context) {
        Result result = contextEvaluator.getValue(context);
        if (contextEvaluator instanceof  BooleanContextEvaluator) {
            result.checkResult(value -> ((BooleanValue)value).value, describe());
        } else {
            result.checkResult(Value::isNotNull, this.toString());
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
