package psl.listener;

import objects.misc.CourseID;
import objects.misc.TermYear;
import objects.misc.TimeRange;
import objects.misc.Weekday;
import preferences.constraints.Constraint;
import preferences.context.Condition;
import preferences.context.ContextLevel;
import preferences.evaluators.ContextEvaluator;
import preferences.specification.ConditionalSpecification;
import preferences.specification.ContextualSpecification;
import preferences.specification.Specification;
import preferences.specification.SpecificationList;

import java.util.*;

public class PSLParsingContext {
    Stack<Condition> conditions;
    Stack<Specification> specifications;
    Stack<Constraint> constraints;
    Stack<ContextEvaluator> evaluators;
    ContextLevel contextLevel;
    Set<CourseID> courseIDS;
    ArrayList<TimeRange> timeRanges;
    Set<TermYear> termYears;
    Set<Weekday> weekdays;

    public PSLParsingContext(ContextLevel contextLevel) {
        conditions = new Stack<>();
        specifications = new Stack<>();
        constraints = new Stack<>();
        evaluators = new Stack<>();
        this.contextLevel = contextLevel;
        courseIDS = new HashSet<>();
        timeRanges = new ArrayList<>();
        termYears = new HashSet<>();
        weekdays = new HashSet<>();
    }

    public SpecificationList getSpecificationList() {
        assert constraints.empty() && evaluators.empty();
        return new SpecificationList(new LinkedList<>(specifications));
    }

    public ConditionalSpecification getConditional() {
        assert constraints.empty() && evaluators.empty();
        return new ConditionalSpecification(new LinkedList<>(conditions), new LinkedList<>(specifications));
    }

    public ContextualSpecification getContextual() {
        assert specifications.size() == 1 && constraints.empty() && evaluators.empty();
        return new ContextualSpecification(specifications.firstElement(), contextLevel, conditions, termYears, weekdays);
    }
}
