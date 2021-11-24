package preferences.evaluators;

import preferences.context.Context;
import preferences.result.Result;
import preferences.result.ScalableValue;

public interface TermYearContextEvaluator {
    public abstract Result<ScalableValue.TermYearValue> getValue(Context context);
}
