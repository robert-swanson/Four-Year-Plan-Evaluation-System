package preferences.condition;

import preferences.constraints.RequireableConstraint;
import preferences.context.Context;
import preferences.explanation.Explanation;

public class ConstraintCondition extends Condition {
    RequireableConstraint requireableConstraint;

    public ConstraintCondition(RequireableConstraint requireableConstraint) {
        this.requireableConstraint = requireableConstraint;
    }

    @Override
    public boolean evaluate(Context context) {
        return requireableConstraint.fulfilled(context);
    }

    @Override
    public String describe() {
        return requireableConstraint.toString();
    }

    @Override
    public Explanation explainLastResult() {
        return requireableConstraint.explainLastResult();
    }

    @Override
    public String toString() {
        return requireableConstraint.toString();
    }
}
