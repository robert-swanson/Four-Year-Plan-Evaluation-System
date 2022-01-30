package preferences.specification;

import objects.misc.TermYear;
import objects.misc.Weekday;
import preferences.condition.Condition;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.explanation.context.ContextExplanation;
import preferences.explanation.specification.ContextualSpecificationResultExplanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;
import psl.PSLGenerator;

import java.util.Set;

public class ContextualSpecification extends Specification {
    private final Specification specification;
    private final ContextLevel contextLevel;

    private final ContextualSpecificationType type;
    private final Condition filterCondition;
    private final Set<Weekday> weekdays;
    private final Set<TermYear> termYears;

    private ContextExplanation currentContextExplanation;


    private enum ContextualSpecificationType {
        condition, terms, days, all
    }

    public ContextualSpecification(Specification specification, ContextLevel contextLevel, Condition condition, Set<TermYear> termYears, Set<Weekday> weekdays) {
        this.specification = specification;
        this.contextLevel = contextLevel;

        this.termYears = termYears;
        this.weekdays = weekdays;

        if (condition != null) { // condition as context filter
            this.type = ContextualSpecificationType.condition;
            this.filterCondition = condition;
        } else if (termYears != null && !termYears.isEmpty()) { // specific term years as context filter
            this.type = ContextualSpecificationType.terms;
            this.filterCondition = null;
        } else if (termYears != null && !weekdays.isEmpty()) { // specific weekdays as context filters
            this.type = ContextualSpecificationType.days;
            this.filterCondition = null;
        } else if (contextLevel == ContextLevel.terms || contextLevel == ContextLevel.days) { // all terms
            this.type = ContextualSpecificationType.all;
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
            case all -> context.applyAllFilter(contextLevel);
        };
        currentContextExplanation = context.explainLastResult();
        return applied;
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        if (applyContext(context)) {
            lastScore = specification.evaluate(context, evaluateAll);
            context.unapplyContextFilter();
        } else {
            lastScore = Score.valid();
        }
        return lastScore;
    }

    @Override
    public SpecificationResultExplanation explainLastResult() {
        return new ContextualSpecificationResultExplanation(this, filterCondition, currentContextExplanation, specification);
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
            case condition -> String.format("for %s where %s", contextLevel.toString(), filterCondition.describe());
            case terms -> String.format("for terms %s", termYearsPSL());
            case days -> String.format("for days %s", weekdaysPSL());
            case all -> String.format("for each %s", contextLevel == ContextLevel.days ? "day" : "term");
        };
    }

    private String termYearsPSL() {
        String str = termYears.toString();
        return str.substring(1, str.length()-1);
    }

    private String weekdaysPSL() {
        StringBuilder builder = new StringBuilder();
        weekdays.forEach(weekday -> builder.append(String.format("%ss, ", weekday)));
        return builder.substring(0, builder.length()-2).toLowerCase();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        switch (type) {
            case condition -> generator.addPSL(String.format("for %s where %s ", contextLevel, filterCondition.toPSL()));
            case terms -> generator.addPSL(String.format("for %s ", termYearsPSL()));
            case days -> generator.addPSL(String.format("for %s ", weekdaysPSL()));
            case all -> generator.addPSL(String.format("for each %s ", contextLevel == ContextLevel.days ? "day" : "term"));
        }
        specification.generatePSL(generator);
    }
}
