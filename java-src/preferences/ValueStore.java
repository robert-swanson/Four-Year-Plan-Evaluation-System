package preferences;

import preferences.context.Context;
import preferences.result.Result;
import preferences.evaluators.ScalableContextEvaluator;

import java.util.HashMap;

public class ValueStore {
    private static class KnownValuesKey {
        Context context;
        ScalableContextEvaluator scalableContextEvaluator;

        public KnownValuesKey(Context context, ScalableContextEvaluator scalableContextEvaluator) {
            this.context = context;
            this.scalableContextEvaluator = scalableContextEvaluator;
        }
    }

    private HashMap<KnownValuesKey, Result> knownValues;

    public Result getValue(ScalableContextEvaluator scalableContextEvaluator, Context context) {
        KnownValuesKey key = new KnownValuesKey(context, scalableContextEvaluator);
        if (knownValues.containsKey(key)) {
            return knownValues.get(key);
        } else {
            Result result = scalableContextEvaluator.getValue(context);
            knownValues.put(key, result);
            return result;
        }
    }

    public void putValue(KnownValuesKey key, Result result) {
        knownValues.put(key, result);
    }
}
