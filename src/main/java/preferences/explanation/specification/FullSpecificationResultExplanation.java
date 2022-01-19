package preferences.explanation.specification;

import preferences.context.Context;
import preferences.explanation.Explanation;
import preferences.explanation.context.ContextExplanation;
import preferences.scoring.Score;
import preferences.specification.FullSpecification;
import preferences.specification.Specification;

public class FullSpecificationResultExplanation extends SpecificationResultExplanation {
    ContextExplanation planContext;
    SpecificationResultExplanation specification;

    public FullSpecificationResultExplanation(FullSpecification specification, Specification subspecification, Context context) {
        super(specification);
        this.planContext = context.explainLastResult();
        this.specification = subspecification.explainLastResult();
    }
}
