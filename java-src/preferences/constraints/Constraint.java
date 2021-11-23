package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ContextEvaluator;
import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.explanation.Explainable;
import preferences.result.Result;

public abstract class Constraint implements Explainable {
    public enum ConstraintType { equal, lessThan, greaterThan, lessThanOrEqual, greaterThanOrEqual, booleanConstraint, more, less }
    public final ConstraintType constraintType;

    protected ContextEvaluator contextEvaluator;
    protected ContextLevel contextLevel;

    public Constraint(ContextEvaluator contextEvaluator, ConstraintType constraintType, ContextLevel contextLevel) {
        this.contextEvaluator = contextEvaluator;
        this.constraintType = constraintType;
        this.contextLevel = contextLevel;
    }

    public abstract Result score(Context context);
    public abstract ConstraintResultExplanation explainLastResult();
}