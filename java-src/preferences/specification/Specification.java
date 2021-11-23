package preferences.specification;

import preferences.context.Context;
import preferences.explanation.Explainable;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;

public abstract class Specification implements Explainable {
    public abstract Score evaluate(Context context, boolean evaluateAll);
    public abstract SpecificationResultExplanation explainLastResult();

    protected Score lastScore;
    public Score getLastResult() {
        return lastScore;
    }

    public abstract String describe();
    public abstract Specification getSimplifiedSpecification(); // returns null if useless specification
}