package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.result.Result;
import preferences.result.Value;

public class BooleanConstraint extends RequireableConstraint {

    public BooleanConstraint(BooleanContextEvaluator booleanContextEvaluator, ContextLevel contextLevel) {
        super(booleanContextEvaluator, ConstraintType.booleanConstraint, contextLevel);
    }

    public BooleanConstraint(TermYearContextEvaluator termYearContextEvaluator, ContextLevel contextLevel) {
        super(termYearContextEvaluator, ConstraintType.booleanConstraint, contextLevel);
    }

    @Override
    public Result score(Context context) {
        if (contextEvaluator instanceof  BooleanContextEvaluator) {
            Result result = contextEvaluator.getValue(context);
            result.scoreResult(value -> ((Value.Boolean)value).value ? 1 : 0, "boolean", this.toString());
            return result;
        } else {
            Result result = contextEvaluator.getValue(context);
            result.scoreResult(value -> value == Value.NULL? 0 : 1, "TermYear not empty", this.toString());
            return result;
        }
    }

    @Override
    public Result fulfilled(Context context) {
        return null;
    }

    @Override
    public ConstraintResultExplanation explainLastResult() {
        return new ConstraintResultExplanation(this);
        // TODO: Add evaluation explanation
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
