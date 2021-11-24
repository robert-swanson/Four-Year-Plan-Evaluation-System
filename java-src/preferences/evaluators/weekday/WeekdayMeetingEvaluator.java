package preferences.evaluators.weekday;

import objects.misc.Weekday;
import preferences.context.Context;
import preferences.context.WeekSubContext;
import preferences.evaluators.ContextEvaluator;
import preferences.evaluators.general.MeetingAccumulatorEvaluator;
import preferences.context.iterables.meeting.PlanTermWeekdayMeetingIterable;
import preferences.context.iterables.meeting.TermWeekdayMeetingIterable;
import preferences.context.iterables.meeting.WeekdayMeetingIterable;
import preferences.result.DaysResult;
import preferences.result.Result;
import preferences.result.Value;

import java.util.ArrayList;

public abstract class WeekdayMeetingEvaluator extends ContextEvaluator {
    public interface WeekdayValue {
        Value get(WeekdayMeetingIterable meetings, Weekday weekday);
    }

    protected Result getValue(Context context, String description, WeekdayValue weekdayValue) {
        return switch (context.getContextLevel()) {
            case fullPlan -> getDaysValue(context, description + " for plan", weekdayValue);
            case terms -> getDaysValue(context, description + " for terms in context", weekdayValue);
            case days -> getDaysValue(context, description + " for weekdays in context", weekdayValue);
        };
    }

    private DaysResult<Value> getDaysValue(Context context, String description, WeekdayValue weekdayValue) {
        DaysResult<Value> result = new DaysResult<>(description);
        StringBuilder explanation = new StringBuilder();
        PlanTermWeekdayMeetingIterable termIterator = context.termWeekdayMeetingIterable();
        for (TermWeekdayMeetingIterable termWeekdayMeetingIterable : termIterator) {
            WeekSubContext currentWeek = null;
            ArrayList<ArrayList<Value>> termValues = new ArrayList<>();
            ArrayList<Value> currentWeekValue = new ArrayList<>();
            ArrayList<Double> weights = new ArrayList<>();

            explanation.append(String.format("  %s:\n", termIterator.getTermYear()));
            for (WeekdayMeetingIterable weekdayMeetingIterable : termWeekdayMeetingIterable) {
                if (!termWeekdayMeetingIterable.getCurrentWeekSubContext().equals(currentWeek)) {
                    if (currentWeek != null) {
                        termValues.add(currentWeekValue);
                        currentWeekValue = new ArrayList<>();
                    }
                    currentWeek = termWeekdayMeetingIterable.getCurrentWeekSubContext();
                    weights.add(currentWeek.getWeight());
                    explanation.append(String.format("    Week %d %s\n", weights.size(), currentWeek));
                }

                Weekday weekday = termWeekdayMeetingIterable.getCurrentWeekday();
                explanation.append(String.format("      %s\n", weekday));

                Value dayVal = weekdayValue.get(weekdayMeetingIterable, weekday);
                currentWeekValue.add(dayVal);
                explanation.append(String.format("        Day Value: %s\n", dayVal));
            }
            if (currentWeek != null) {
                termValues.add(currentWeekValue);
                weights.add(currentWeek.getWeight());
            }
            result.addTermValue(termValues, weights);
        }
        result.setResultExplanation(explanation.toString());
        return result;
    }
}
