package preferences.explanation.constraints;

import preferences.constraints.Constraint;
import preferences.evaluators.ContextEvaluator;
import preferences.explanation.Explanation;

public class ConstraintResultExplanation extends Explanation {
    ContextEvaluatorResultExplanation evaluator;

    public ConstraintResultExplanation(Constraint constraint, ContextEvaluator contextEvaluator) {
        super(constraint);
        this.evaluator = contextEvaluator.explainLastResult();
    }
}
