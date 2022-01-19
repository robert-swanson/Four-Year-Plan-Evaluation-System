package preferences.context;

import objects.misc.TermYear;
import objects.misc.Weekday;
import objects.plan.Plan;
import objects.plan.PlanTerm;
import preferences.explanation.Explainable;
import preferences.explanation.context.ContextExplanation;
import preferences.context.iterables.courseoffering.PlanCourseOfferingIterator;
import preferences.context.iterables.courseoffering.PlanTermCourseOfferingIterable;
import preferences.context.iterables.courseoffering.PlanTermWeekdayCourseOfferingIterable;
import preferences.context.iterables.meeting.PlanMeetingIterator;
import preferences.context.iterables.meeting.PlanTermMeetingIterable;
import preferences.context.iterables.meeting.PlanTermWeekdayMeetingIterable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class Context implements Explainable {
    // Non-Contextual
    private final Plan plan;

    // Contextual
    private final Stack<ContextLevel> contextLevelStack;
    private final Stack<LinkedHashMap<TermYear, TermSubContext>> termSubContexts;

    public Context(Plan plan) {
        // Non-Contextual
        this.plan = plan;

        // Contextual
        contextLevelStack = new Stack<>();
        contextLevelStack.push(ContextLevel.fullPlan);

        termSubContexts = new Stack<>();
        LinkedHashMap<TermYear, TermSubContext> termSubContextLinkedHashMap = new LinkedHashMap<>();
        for (Map.Entry<TermYear, PlanTerm> entry : plan.getTermsMap().entrySet()) {
            termSubContextLinkedHashMap.put(entry.getKey(), new TermSubContext(entry.getValue()));
        }
        termSubContexts.push(termSubContextLinkedHashMap);
    }

    private Context(TermSubContext termSubContext) { // Used for Terms Context Filtering
        plan = null;
        contextLevelStack = new Stack<>();
        contextLevelStack.push(ContextLevel.terms);
        termSubContexts = new Stack<>();
        LinkedHashMap<TermYear, TermSubContext> termSubContextLinkedHashMap = new LinkedHashMap<>();
        termSubContextLinkedHashMap.put(termSubContext.getTermYear(), termSubContext);
        termSubContexts.push(termSubContextLinkedHashMap);
    }

    protected Context(TermSubContext termSubContext, WeekSubContext weekSubContext, WeekdaySubContext weekdaySubContext) { // Used for Weekday Context Filtering
        plan = null;
        contextLevelStack = new Stack<>();
        contextLevelStack.push(ContextLevel.days);
        TermSubContext singleWeekdayTerm = new TermSubContext(termSubContext, weekSubContext, weekdaySubContext);
        LinkedHashMap<TermYear, TermSubContext> singleWeekdayContext = new LinkedHashMap<>();
        singleWeekdayContext.put(termSubContext.getTermYear(), singleWeekdayTerm);
        termSubContexts = new Stack<>();
        termSubContexts.push(singleWeekdayContext);
    }

    // returns false if no change due to empty resulting context
    public boolean applyContextFilter(Condition condition, ContextLevel contextLevel) {
        assert contextLevel != ContextLevel.fullPlan;
        if (contextLevel == ContextLevel.terms) {
            LinkedHashMap<TermYear, TermSubContext> newContext = new LinkedHashMap<>();
            for (TermSubContext termSubContext : termSubContexts.peek().values()) {
                Context tempContext = new Context(termSubContext);
                if (condition.evaluate(tempContext)) {
                    newContext.put(termSubContext.getTermYear(), termSubContext);
                }
            }
            if (newContext.isEmpty()) {
                return false;
            } else {
                termSubContexts.push(newContext);
                contextLevelStack.push(ContextLevel.terms);
                return true;
            }
        } else if (contextLevel == ContextLevel.days) {
            LinkedHashMap<TermYear, TermSubContext> newContext = new LinkedHashMap<>();
            for (TermSubContext termSubContext : termSubContexts.peek().values()) {
                if (termSubContext.applyContextFilter(condition)) {
                    newContext.put(termSubContext.getTermYear(), termSubContext);
                }
            }
            if (newContext.isEmpty()) {
                return false;
            } else {
                termSubContexts.push(newContext);
                contextLevelStack.push(contextLevel);
                return true;
            }
        } else  {
            assert false;
            return false;
        }
    }

    public boolean filterTerms(Set<TermYear> termYearSet) {
        assert contextLevelStack.peek() != ContextLevel.days;
        LinkedHashMap<TermYear, TermSubContext> newContext = new LinkedHashMap<>();
        termYearSet.forEach(termYear -> {
            TermSubContext termSubContext = termSubContexts.peek().get(termYear);
            if (termSubContext != null) {
                newContext.put(termYear, termSubContext);
            }
        });
        if (newContext.isEmpty()) {
            return false;
        } else {
            termSubContexts.push(newContext);
            contextLevelStack.push(ContextLevel.terms);
            return true;
        }
    }

    public boolean filterDays(Set<Weekday> weekdaySet) {
        LinkedHashMap<TermYear, TermSubContext> newContext = new LinkedHashMap<>();
        for (TermSubContext termSubContext : termSubContexts.peek().values()) {
            if (termSubContext.filterDays(weekdaySet)) {
                newContext.put(termSubContext.getTermYear(), termSubContext);
            }
        }

        if (newContext.isEmpty()) {
            return false;
        } else {
            termSubContexts.push(newContext);
            contextLevelStack.push(ContextLevel.days);
            return true;
        }
    }

    public void unapplyContextFilter() {
        LinkedHashMap<TermYear, TermSubContext> oldTerms = termSubContexts.pop();
        if (contextLevelStack.pop() == ContextLevel.days) {
                oldTerms.values().forEach(TermSubContext::popContext);
        }
    }

    // Plan Info
    public Set<TermYear> getTerms() {
        return termSubContexts.peek().keySet();
    }


    public TermYear lastTerm() {
        TermYear last = null;
        for (TermYear termYear : termSubContexts.peek().keySet()) {
            if (last == null || termYear.compareTo(last) > 0) {
                last = termYear;
            }
        }
        return last;
    }

    // Iterables
    public PlanTermWeekdayMeetingIterable termWeekdayMeetingIterable() {
        return new PlanTermWeekdayMeetingIterable(this);
    }
    public PlanTermMeetingIterable termMeetingIterable() {
        return new PlanTermMeetingIterable(this);
    }
    public PlanMeetingIterator meetingIterator() {
        return new PlanMeetingIterator(this);
    }
    public PlanTermWeekdayCourseOfferingIterable termWeekdayCourseOfferingIterable() {
        return new PlanTermWeekdayCourseOfferingIterable(this);
    }
    public PlanTermCourseOfferingIterable termCourseOfferingIterable() {
        return new PlanTermCourseOfferingIterable(this);
    }
    public PlanCourseOfferingIterator courseOfferingIterator() {
        return new PlanCourseOfferingIterator(this);
    }

    // Getters
    public ContextLevel getContextLevel() {
        return contextLevelStack.peek();
    }

    public LinkedHashMap<TermYear, TermSubContext> getTermSubContexts() {
        return termSubContexts.peek();
    }

    public void assertPlanContext() {
        assert contextLevelStack.size() == 1 : "Context Level Stack ≠ 1";
        assert termSubContexts.size() == 1 : "Term Sub Contexts Stack ≠ 1";
        assert contextLevelStack.peek() == ContextLevel.fullPlan: "Context level isn't full plan";
        getTermSubContexts().values().forEach(TermSubContext::assertPlanContext);
    }


    @Override
    public ContextExplanation explainLastResult() {
        return new ContextExplanation(this);
    }

    @Override
    public String describe() {
        return String.format("Context at level: %s", getContextLevel().toString());
    }
}

