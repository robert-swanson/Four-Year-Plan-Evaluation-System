package preferences.evaluators;

import preferences.context.ContextLevel;

public interface ScalableContextEvaluator extends ContextEvaluator {
    double getDeviance(ContextLevel contextLevel);
    double getAverage(ContextLevel contextLevel);
}
