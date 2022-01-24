package preferences.context;

import objects.misc.Date;
import objects.misc.Weekday;
import objects.offerings.Meeting;
import preferences.condition.Condition;
import preferences.explanation.Explainable;
import preferences.explanation.context.WeekSubContextExplanation;

import java.util.*;

/**
 * Contains Meetings and CourseOfferings for a segment of the semester which has the same schedule (sub context: weekdays)
 */
public class WeekSubContext implements Explainable {
    // Non-Contextual
    Date startDate, endDate;
    Double weight;

    // Contextual
    Stack<LinkedHashMap<Weekday, WeekdaySubContext>> weekdaySubcontextsStack;

    public WeekSubContext(Date startDate, Date endDate, LinkedList<Meeting> meetings, double weight) {
        // Non-Contextual
        this.startDate = startDate;
        this.endDate = endDate;
        this.weight = weight;

        // Contextual
        HashMap<Weekday, LinkedList<Meeting>> daysToMeetings = new HashMap<>();
        for (Meeting meeting : meetings) {
            for (Weekday weekday : meeting.getDays()) {
                if (!daysToMeetings.containsKey(weekday)) {
                    daysToMeetings.put(weekday, new LinkedList<>());
                }
                daysToMeetings.get(weekday).add(meeting);
            }
        }

        LinkedHashMap<Weekday, WeekdaySubContext> weekdaySubcontexts = new LinkedHashMap<>();
        for (Weekday weekday: Weekday.values()) {
            if (daysToMeetings.containsKey(weekday)) {
                weekdaySubcontexts.put(weekday, new WeekdaySubContext(weekday, daysToMeetings.get(weekday)));
            }
        }
        weekdaySubcontextsStack = new Stack<>();
        weekdaySubcontextsStack.push(weekdaySubcontexts);
    }

    public WeekSubContext(WeekSubContext weekSubContext, WeekdaySubContext weekdaySubContext) { // Used for Weekday Context Filtering
        startDate = weekSubContext.startDate;
        endDate = weekSubContext.endDate;
        LinkedHashMap<Weekday, WeekdaySubContext> singleWeekdayContext = new LinkedHashMap<>();
        singleWeekdayContext.put(weekdaySubContext.weekday, weekdaySubContext);
        weekdaySubcontextsStack = new Stack<>();
        weekdaySubcontextsStack.push(singleWeekdayContext);
    }

    // Context Filtering
    public boolean applyContextFilter(Condition condition, TermSubContext termSubContext) {
        LinkedHashMap<Weekday, WeekdaySubContext> newContext = new LinkedHashMap<>();
        for (WeekdaySubContext weekdaySubContext : weekdaySubcontextsStack.peek().values()) {
            Context tempContext = new Context(termSubContext, this, weekdaySubContext);
            if (condition.evaluate(tempContext)) {
                newContext.put(weekdaySubContext.weekday, weekdaySubContext);
            }
        }
        if (newContext.isEmpty()) {
            return false;
        } else {
            weekdaySubcontextsStack.push(newContext);
            return true;
        }
    }

    public boolean filterDays(Set<Weekday> weekdaySet) {
        LinkedHashMap<Weekday, WeekdaySubContext> newContext = new LinkedHashMap<>();
        weekdaySet.forEach(weekday -> {
            WeekdaySubContext weekdaySubContext = weekdaySubcontextsStack.peek().get(weekday);
            if (weekdaySubContext != null) {
                newContext.put(weekday, weekdaySubContext);
            }
        });
        if (newContext.isEmpty()) {
            return false;
        } else {
            weekdaySubcontextsStack.push(newContext);
            return true;
        }
    }

    public void duplicateStackTop() {
        weekdaySubcontextsStack.push(getWeekdaySubcontexts());
    }

    public void popContext() {
        weekdaySubcontextsStack.pop();
    }

    // Getters
    public Double getWeight() {
        return weight;
    }

    public LinkedHashMap<Weekday, WeekdaySubContext> getWeekdaySubcontexts() {
        return weekdaySubcontextsStack.peek();
    }

    public void assertPlanContext() {
        assert weekdaySubcontextsStack.size() == 1 : "WeekSubContext's WeekdaySubContextsStack ≠ 1";
    }

    @Override
    public WeekSubContextExplanation explainLastResult() {
        return new WeekSubContextExplanation(this);
    }

    @Override
    public String describe() {
        if (startDate == null && endDate == null) {
            return "full term";
        } else if (startDate == null) {
            return String.format("until %s", endDate);
        } else if (endDate == null) {
            return String.format("starting %s", startDate);
        } else {
            return String.format("%s through %s", startDate, endDate);
        }
    }

    @Override
    public String toString() {
        return describe();
    }
}
