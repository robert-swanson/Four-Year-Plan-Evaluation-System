package preferences.evaluators;

import preferences.context.Context;
import preferences.result.Result;
import preferences.value.BooleanValue;

public abstract class BooleanContextEvaluator extends ContextEvaluator {
    public abstract Result<BooleanValue> getValue(Context context);
}
