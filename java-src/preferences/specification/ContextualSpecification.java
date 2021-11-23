package preferences.specification;

import objects.misc.TermYear;
import objects.misc.Weekday;
import preferences.context.Condition;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.explanation.specification.ContextualSpecificationResultExplanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;

import java.util.Set;
import java.util.Stack;

public class ContextualSpecification extends Specification {
    private final Specification specification;
    private final ContextLevel contextLevel;

    private final ContextualSpecificationType type;
    private final Condition filterCondition;
    private final Set<Weekday> weekdays;
    private final Set<TermYear> termYears;

    private Context currentContext;

    private enum ContextualSpecificationType {
        condition, terms, days
    }

    public ContextualSpecification(Specification specification, ContextLevel contextLevel, Stack<Condition> condition, Set<TermYear> termYears, Set<Weekday> weekdays) {
        this.specification = specification;
        this.contextLevel = contextLevel;

        this.termYears = termYears;
        this.weekdays = weekdays;

        if (!condition.isEmpty()) {
            this.type = ContextualSpecificationType.condition;
            this.filterCondition = condition.peek();
        } else if (!termYears.isEmpty()) {
            this.type = ContextualSpecificationType.terms;
            this.filterCondition = null;
        } else if (!weekdays.isEmpty()) {
            this.type = ContextualSpecificationType.days;
            this.filterCondition = null;
        } else {
            this.type = null;
            this.filterCondition = null;
            assert false : "No filter provided to contextual specification";
        }
    }

    private boolean applyContext(Context context) {
        boolean applied = switch (type) {
            case condition -> context.applyContextFilter(filterCondition, contextLevel);
            case terms -> context.filterTerms(termYears);
            case days -> context.filterDays(weekdays);
        };
        currentContext = context;
        return applied;
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        if (applyContext(context)) {
            lastScore = specification.evaluate(context, evaluateAll);
            context.unapplyContextFilter();
        } else {
            lastScore = Score.VALID;
        }
        return lastScore;
    }

    @Override
    public SpecificationResultExplanation explainLastResult() {
        return new ContextualSpecificationResultExplanation(this, filterCondition, currentContext, specification);
    }

    @Override
    public Specification getSimplifiedSpecification() {
        if (specification.getSimplifiedSpecification() == null) {
            return null;
        } else {
            return this;
        }
    }

    @Override
    public String describe() {
        return switch (type) {
            case condition -> String.format("for %s where (%s)", contextLevel.toString(), filterCondition.toString());
            case terms -> String.format("for terms %s", termYears);
            case days -> String.format("for days %s", weekdays);
        };
    }

    @Override
    public String toString() {
        return describe() + " " + specification.toString();
    }
}