package preferences.explanation.specification;

import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.specification.RequirementSpecification;

public class RequirementSpecificationResultExplanation extends SpecificationResultExplanation {
    ConstraintResultExplanation constraint;

    public RequirementSpecificationResultExplanation(RequirementSpecification specification, ConstraintResultExplanation constraint) {
        super(specification);
        this.constraint = constraint;
    }
}
