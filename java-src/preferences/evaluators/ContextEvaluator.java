package preferences.evaluators;

import preferences.context.Context;
import preferences.result.Result;

public interface ContextEvaluator {
    Result getValue(Context context);
}
