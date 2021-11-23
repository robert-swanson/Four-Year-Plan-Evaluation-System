package preferences.explanation.specification;

import preferences.explanation.ConditionalClauseResultExplanation;
import preferences.specification.ConditionalSpecification;

import java.util.LinkedList;

public class ConditionalSpecificationResultExplanation extends SpecificationResultExplanation {
    LinkedList<ConditionalClauseResultExplanation> conditionalClauses;
    public ConditionalSpecificationResultExplanation (ConditionalSpecification specification, LinkedList<ConditionalSpecification.ConditionalClause> clauses) {
        super(specification);
        conditionalClauses = new LinkedList<>();
        clauses.forEach(clause -> conditionalClauses.add(clause.explainLastResult()));
    }
}
