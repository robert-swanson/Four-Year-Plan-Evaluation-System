package preferences.evaluators;

import preferences.context.Context;
import preferences.explanation.constraints.ContextEvaluatorResultExplanation;
import preferences.explanation.Explainable;
import preferences.result.Result;

public abstract class ContextEvaluator implements Explainable {
    abstract public Result getValue(Context context);
    public abstract String describe();

    public ContextEvaluatorResultExplanation explainLastResult() {
        return new ContextEvaluatorResultExplanation(this);
    }

    protected Result lastValue;
    public Result getLastValue() {
        return lastValue;
    }

    @Override
    public String toString() {
        return describe();
    }
}
