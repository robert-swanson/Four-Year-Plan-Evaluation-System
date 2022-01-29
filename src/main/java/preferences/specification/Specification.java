package preferences.specification;

import preferences.context.Context;
import preferences.explanation.Explainable;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;
import psl.PSLComponent;

public abstract class Specification implements Explainable, PSLComponent {
    public abstract Score evaluate(Context context, boolean evaluateAll);

    protected Score lastScore;
    public Score getLastResult() { return lastScore; }
    public abstract SpecificationResultExplanation explainLastResult();

    public abstract String describe();

    public abstract Specification getSimplifiedSpecification();

    @Override
    public String toString() {
        return toPSL();
    }
}