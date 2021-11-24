package preferences.evaluators.general;

import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.context.WeekSubContext;
import preferences.evaluators.ContextEvaluator;
import preferences.context.iterables.meeting.*;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.*;

import java.util.ArrayList;

public abstract class MeetingAccumulatorEvaluator extends ScalableContextEvaluator {
    public interface MeetingAttribute {
        double get(Meeting meeting);
    }

    static Result getValue(Context context, String description, MeetingAttribute meetingAttribute) {
        return switch (context.getContextLevel()) {
            case fullPlan -> getPlanValue(context, description + " for plan", meetingAttribute);
            case terms -> getTermsValue(context, description + " for terms in context", meetingAttribute);
            case days -> getDaysValue(context, description + " for weekdays in context", meetingAttribute);
        };
    }

    static PlanResult<ScalableValue.Numeric> getPlanValue(Context context, String description, MeetingAttribute meetingAttribute) {
        int accumulator = 0;
        StringBuilder explanation = new StringBuilder();
        for (Meeting meeting : context.meetingIterator()) {
            accumulator += meetingAttribute.get(meeting);
            explanation.append(String.format("  class \"%s\"\n", meeting));
        }
        return new PlanResult<>(new ScalableValue.Numeric(accumulator));
    }

    static TermsResult<ScalableValue.Numeric> getTermsValue(Context context, String description, MeetingAttribute meetingAttribute) {
        TermsResult<ScalableValue.Numeric> result = new TermsResult<>(description);
        StringBuilder explanation = new StringBuilder();
        PlanTermMeetingIterable iterable = context.termMeetingIterable();
        for (TermMeetingIterable termMeetingIterable : iterable) {
            explanation.append(String.format("    %s:\n", iterable.getTermYear().toString()));
            double accumulator = 0;
            for (Meeting meeting : termMeetingIterable) {
                accumulator += meetingAttribute.get(meeting);
                explanation.append(String.format("      %s\n", meeting));
            }
            result.addValue(new ScalableValue.Numeric(accumulator));
            explanation.append(String.format("      Term Count: %.2f\n", accumulator));
        }
        return result;
    }

    static DaysResult<ScalableValue.Numeric> getDaysValue(Context context, String description, MeetingAttribute meetingAttribute) {
        DaysResult<ScalableValue.Numeric> result = new DaysResult<>();
        StringBuilder explanation = new StringBuilder();
        PlanTermWeekdayMeetingIterable termIterator = context.termWeekdayMeetingIterable();
        for (TermWeekdayMeetingIterable termWeekdayMeetingIterable : termIterator) {
            WeekSubContext currentWeek = null;
            ArrayList<ArrayList<ScalableValue.Numeric>> termValues = new ArrayList<>();
            ArrayList<ScalableValue.Numeric> currentWeekValue = new ArrayList<>();
            ArrayList<Double> weights = new ArrayList<>();

            explanation.append(String.format("  %s:\n", termIterator.getTermYear()));
            for (WeekdayMeetingIterable weekdayMeetingIterable : termWeekdayMeetingIterable) {
                double accumulator = 0;

                if (!termWeekdayMeetingIterable.getCurrentWeekSubContext().equals(currentWeek)) {
                    if (currentWeek != null) {
                        termValues.add(currentWeekValue);
                        currentWeekValue = new ArrayList<>();
                    }
                    currentWeek = termWeekdayMeetingIterable.getCurrentWeekSubContext();
                    weights.add(currentWeek.getWeight());
                    explanation.append(String.format("    Week %d %s\n", weights.size(), currentWeek));
                }

                explanation.append(String.format("      %s\n", termWeekdayMeetingIterable.getCurrentWeekday()));

                for (Meeting meeting : weekdayMeetingIterable) {
                    accumulator += meetingAttribute.get(meeting);
                    explanation.append(String.format("        %s\n", meeting));
                }

                currentWeekValue.add(new ScalableValue.Numeric(accumulator));
                explanation.append(String.format("        Day Count: %.2f\n", accumulator));
            }
            if (currentWeek != null) {
                termValues.add(currentWeekValue);
                weights.add(currentWeek.getWeight());
            }
            result.addTermValue(termValues, weights);
        }
        return result;
    }
}
