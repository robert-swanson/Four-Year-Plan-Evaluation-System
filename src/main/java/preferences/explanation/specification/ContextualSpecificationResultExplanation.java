package preferences.explanation.specification;

import preferences.condition.Condition;
import preferences.explanation.Explanation;
import preferences.explanation.context.ContextExplanation;
import preferences.specification.ContextualSpecification;
import preferences.specification.Specification;

import java.util.Objects;

public class ContextualSpecificationResultExplanation extends SpecificationResultExplanation {
    Explanation condition; // TODO: show condition results for each sub-context
    Explanation context;
    SpecificationResultExplanation specification;
    public ContextualSpecificationResultExplanation(ContextualSpecification specification, Condition condition, ContextExplanation contextExplanation, Specification subspecification) {
        super(specification);
        if (condition != null) {
            this.condition = condition.explainLastResult();
        }
        this.context = Objects.requireNonNullElseGet(contextExplanation, NotComputed::new);
        this.specification = subspecification.explainLastResult();
    }
}
