package preferences.explanation.specification;

import preferences.specification.Specification;

import java.util.LinkedList;

public class SpecificationListResultExplanation extends SpecificationResultExplanation {
    LinkedList<SpecificationResultExplanation> specifications;

    public SpecificationListResultExplanation(Specification specification, LinkedList<Specification> specifications) {
        super(specification);
        this.specifications = new LinkedList<>();
        specifications.forEach(subSpec -> this.specifications.add(subSpec.explainLastResult()));
    }
}
