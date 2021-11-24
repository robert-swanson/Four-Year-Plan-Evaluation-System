package preferences.explanation.constraints;

import preferences.constraints.Constraint;
import preferences.evaluators.ContextEvaluator;
import preferences.explanation.Explanation;

public class ConstraintResultExplanation extends Explanation {
    ContextEvaluatorResultExplanation contextEvaluator;

    public ConstraintResultExplanation(Constraint constraint, ContextEvaluator contextEvaluator) {
        super(constraint);
        this.contextEvaluator = contextEvaluator.explainLastResult();
    }
}
