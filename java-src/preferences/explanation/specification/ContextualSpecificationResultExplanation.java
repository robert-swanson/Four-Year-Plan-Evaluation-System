package preferences.explanation.specification;

import preferences.context.Condition;
import preferences.context.Context;
import preferences.explanation.Explanation;
import preferences.explanation.context.ContextExplanation;
import preferences.specification.ContextualSpecification;
import preferences.specification.Specification;

public class ContextualSpecificationResultExplanation extends SpecificationResultExplanation {
    Explanation condition; // TODO: show condition results for each sub-context
    ContextExplanation context;
    SpecificationResultExplanation specification;
    public ContextualSpecificationResultExplanation(ContextualSpecification specification, Condition condition, Context context, Specification subspecification) {
        super(specification);
        if (condition != null) {
            this.condition = condition.explainLastResult();
        }
        this.context = context.explainLastResult();
        this.specification = subspecification.explainLastResult();
    }
}
