package preferences.evaluators;

import preferences.context.Context;
import preferences.explanation.constraints.ContextEvaluatorResultExplanation;
import preferences.explanation.Explainable;
import preferences.result.Result;
import psl.PSLComponent;

public abstract class ContextEvaluator implements Explainable, PSLComponent {
    abstract public Result getValue(Context context);

    public ContextEvaluatorResultExplanation explainLastResult() {
        return new ContextEvaluatorResultExplanation(this);
    }

    protected Result lastResult;
    public Result getLastResult() {
        return lastResult;
    }

    @Override
    public String toString() {
        return toPSL();
    }

    public String describe() {
        return toPSL();
    }

}
