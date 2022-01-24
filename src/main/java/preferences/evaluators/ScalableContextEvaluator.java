package preferences.evaluators;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.Result;
import preferences.value.ScalableValue;

public abstract class ScalableContextEvaluator extends ContextEvaluator {
    public abstract double getDeviance(ContextLevel contextLevel);
    public abstract double getAverage(ContextLevel contextLevel);
    public abstract Result<ScalableValue> getValue(Context context);
}
