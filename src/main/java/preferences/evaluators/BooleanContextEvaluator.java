package preferences.evaluators;

import preferences.context.Context;
import preferences.result.Result;
import preferences.result.ScalableValue;
import preferences.result.Value;

public abstract class BooleanContextEvaluator extends ContextEvaluator {
    public abstract Result<Value.Boolean> getValue(Context context);
}
