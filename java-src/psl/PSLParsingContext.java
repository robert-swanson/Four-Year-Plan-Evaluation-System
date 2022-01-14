package psl;

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
import psl.exceptions.ListenerError;

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

    public static class ContextAssertionError extends ListenerError {
        public ContextAssertionError(String assertion, String problem){
            super(String.format("ERROR: Listener attempted to assert %s, but %s", assertion, problem));
        }

        public static void assertSpecificationList(PSLParsingContext context) {
            if (!context.constraints.empty()) {
                throw new ContextAssertionError("context as SpecificationList", "found constraints");
            } else if (!context.evaluators.empty()) {
                throw new ContextAssertionError("context as SpecificationList", "found evaluators");
            }
        }

        public static void assertConditional(PSLParsingContext context) {
            if (!context.constraints.empty()) {
                throw new ContextAssertionError("context as Conditional", "found constraints");
            } else if (!context.evaluators.empty()) {
                throw new ContextAssertionError("context as Conditional", "found evaluators");
            }
        }

        public static void assertContextual(PSLParsingContext context) {
            if (!context.constraints.empty()) {
                throw new ContextAssertionError("context as Contextual", "found constraints");
            } else if (!context.evaluators.empty()) {
                throw new ContextAssertionError("context as Contextual", "found evaluators");
            } else if (context.specifications.size() > 1) {
                throw new ContextAssertionError("context as Contextual", "found too many specifications");
            } else if (context.specifications.isEmpty()) {
                throw new ContextAssertionError("context as Contextual", "couldn't find a specification");
            }
        }
    }

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
        ContextAssertionError.assertSpecificationList(this);
        return new SpecificationList(new LinkedList<>(specifications));
    }

    public ConditionalSpecification getConditional() {
        ContextAssertionError.assertConditional(this);
        return new ConditionalSpecification(new LinkedList<>(conditions), new LinkedList<>(specifications));
    }

    public ContextualSpecification getContextual() {
        ContextAssertionError.assertContextual(this);
        Condition condition = conditions.isEmpty() ? null : conditions.firstElement();
        return new ContextualSpecification(specifications.firstElement(), contextLevel, condition, termYears, weekdays);
    }
}
