package preferences.specification;

import preferences.context.Context;
import preferences.explanation.Explainable;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;

public abstract class Specification implements Explainable {
    protected Score lastScore;
    public abstract String describe();
    public abstract Specification getSimplifiedSpecification();
    public abstract Score evaluate(Context context, boolean evaluateAll);
    public abstract SpecificationResultExplanation explainLastResult();
    public Score getLastResult() { return lastScore; }
}