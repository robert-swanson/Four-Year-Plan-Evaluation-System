package preferences.explanation.constraints;

import preferences.evaluators.ContextEvaluator;
import preferences.explanation.Explanation;
import preferences.result.Result;

public class ContextEvaluatorResultExplanation extends Explanation {
    String result;
    public ContextEvaluatorResultExplanation(ContextEvaluator contextEvaluator) {
        super(contextEvaluator);
        Result result = contextEvaluator.getLastResult();
        this.result = (result == null ? "not computed" : result.describe());
    }
}
