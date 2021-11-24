package preferences.specification;

import preferences.context.Condition;
import preferences.context.Context;
import preferences.explanation.specification.ConditionalClauseResultExplanation;
import preferences.explanation.Explainable;
import preferences.explanation.specification.ConditionalSpecificationResultExplanation;
import preferences.scoring.Score;

import java.util.LinkedList;

public class ConditionalSpecification extends Specification {
    public static class ConditionalClause implements Explainable {
        public final Condition condition;
        public final Specification specification;

        public ConditionalClause(Condition c, Specification s) {
            condition = c;
            specification = s;
        }

        @Override
        public ConditionalClauseResultExplanation explainLastResult() {
            return new ConditionalClauseResultExplanation(this);
        }

        public String describe() {
            return "if " + condition.describe();
        }
    }
    private final LinkedList<ConditionalClause> conditionalClauses;

    private ConditionalSpecification(LinkedList<ConditionalClause> conditionalClauses) {
        this.conditionalClauses = conditionalClauses;
    }

    public ConditionalSpecification(LinkedList<Condition> conditions, LinkedList<Specification> specifications) {
        assert (conditions.size() == specifications.size()) || (conditions.size() == specifications.size() - 1);
        conditionalClauses = new LinkedList<>();
        for (int i = 0; i < conditions.size(); i++) {
            conditionalClauses.add(new ConditionalClause(conditions.get(i), specifications.get(i)));
        }
        if (conditions.size() == specifications.size() - 1) {
            conditionalClauses.add(new ConditionalClause(Condition.True, specifications.get(specifications.size()-1)));
        }
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        for (ConditionalClause conditionalClause : conditionalClauses) {
            if (conditionalClause.condition.evaluate(context)) {
                lastScore = conditionalClause.specification.evaluate(context, evaluateAll);
                return lastScore;
            }
        }
        lastScore = Score.valid();
        return lastScore;
    }

    @Override
    public ConditionalSpecificationResultExplanation explainLastResult() {
        return new ConditionalSpecificationResultExplanation(this, conditionalClauses);
    }

    @Override
    public Specification getSimplifiedSpecification() {
        LinkedList<ConditionalClause> newClauses = new LinkedList<>(conditionalClauses);
        newClauses.removeIf(conditionalClause -> conditionalClause.specification.getSimplifiedSpecification() == null);
        if (newClauses.isEmpty()) return null;
        else return new ConditionalSpecification(newClauses);
    }

    @Override
    public String describe() {
        return "Conditional Specification";
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder(String.format("if %s then %s", conditionalClauses.getFirst().condition, conditionalClauses.getFirst().specification));
        for (ConditionalClause clause : conditionalClauses) {
            if (clause.condition.equals(Condition.True)) {
                builder.append(String.format("\notherwise %s", clause.specification));
            } else if (clause != conditionalClauses.getFirst()) {
                builder.append(String.format("\notherwise if %s then %s", clause.condition, clause.specification));
            }
        }
        return builder.toString();
    }
}
