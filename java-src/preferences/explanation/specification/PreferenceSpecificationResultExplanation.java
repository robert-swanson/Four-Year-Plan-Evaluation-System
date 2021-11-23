package preferences.explanation.specification;

import preferences.explanation.constraints.ConstraintResultExplanation;
import preferences.specification.PreferenceSpecification;

public class PreferenceSpecificationResultExplanation extends SpecificationResultExplanation {
    String specification;
    ConstraintResultExplanation constraint;

    public PreferenceSpecificationResultExplanation(PreferenceSpecification specification, ConstraintResultExplanation constraint) {
        super(specification);
        this.constraint = constraint;
    }
}
