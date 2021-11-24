package preferences.specification;

import preferences.context.Context;
import preferences.explanation.specification.FullSpecificationResultExplanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;

public class FullSpecification extends Specification {
    Specification specification;
    Context lastContext;
    transient String name;

    public FullSpecification(Specification specification, String name) {
        this.specification = specification;
        this.name = name;
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        lastContext = context;
        lastScore = specification.evaluate(context, evaluateAll);
        return lastScore;
    }

    @Override
    public FullSpecificationResultExplanation explainLastResult() {
        return new FullSpecificationResultExplanation(this, specification, lastContext);
    }

    @Override
    public String describe() {
        return name;
    }

    @Override
    public Specification getSimplifiedSpecification() {
        return new FullSpecification(specification.getSimplifiedSpecification(), name);
    }
}
