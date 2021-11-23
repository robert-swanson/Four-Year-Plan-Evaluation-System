package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ContextEvaluator;
import preferences.result.Result;

public abstract class RequireableConstraint extends Constraint {
    public RequireableConstraint(ContextEvaluator contextEvaluator, ConstraintType constraintType, ContextLevel contextLevel) {
        super(contextEvaluator, constraintType, contextLevel);
    }

    public abstract Result fulfilled(Context context);
}
