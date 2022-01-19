package preferences.explanation.specification;

import preferences.explanation.Explanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.specification.ConditionalSpecification;

public class ConditionalClauseResultExplanation extends Explanation {
    Explanation condition;
    SpecificationResultExplanation specification;
    public ConditionalClauseResultExplanation(ConditionalSpecification.ConditionalClause clause) {
        super(clause);
        this.condition = clause.condition.explainLastResult();
        this.specification = clause.specification.explainLastResult();
    }
}
