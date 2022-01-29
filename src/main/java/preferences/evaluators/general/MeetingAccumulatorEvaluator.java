package preferences.evaluators.general;

import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.context.WeekSubContext;
import preferences.context.iterables.meeting.*;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.DaysResult;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.result.TermsResult;
import preferences.value.NumericValue;

import java.util.ArrayList;

public abstract class MeetingAccumulatorEvaluator extends NumericContextEvaluator {
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

    static PlanResult<NumericValue> getPlanValue(Context context, String description, MeetingAttribute meetingAttribute) {
        double accumulator = 0;
        StringBuilder explanation = new StringBuilder();
        for (Meeting meeting : context.meetingIterator()) {
            double inc = meetingAttribute.get(meeting);
            accumulator += inc;
            if (inc != 0) explanation.append(String.format("- %s = %.2f\n", meeting, inc));
        }
        return new PlanResult<>(new NumericValue(accumulator).setExplanation(explanation));
    }

    static TermsResult<NumericValue> getTermsValue(Context context, String description, MeetingAttribute meetingAttribute) {
        TermsResult<NumericValue> result = new TermsResult<>(description);
        PlanTermMeetingIterable iterable = context.termMeetingIterable();
        for (TermMeetingIterable termMeetingIterable : iterable) {
            StringBuilder explanation = new StringBuilder();
            double accumulator = 0;
            for (Meeting meeting : termMeetingIterable) {
                double inc = meetingAttribute.get(meeting);
                accumulator += inc;
                if (inc != 0) explanation.append(String.format("- %s = %.2f\n", meeting, inc));
            }
            explanation.append(String.format("= %.2f\n", accumulator));
            result.addValue((NumericValue) new NumericValue(accumulator).setExplanation(explanation));
            explanation.append(String.format("      Term Count: %.2f\n", accumulator));
        }
        return result;
    }

    static DaysResult<NumericValue> getDaysValue(Context context, String description, MeetingAttribute meetingAttribute) {
        DaysResult<NumericValue> result = new DaysResult<>();
        PlanTermWeekdayMeetingIterable termIterator = context.termWeekdayMeetingIterable();
        for (TermWeekdayMeetingIterable termWeekdayMeetingIterable : termIterator) {
            WeekSubContext currentWeek = null;
            ArrayList<ArrayList<NumericValue>> termValues = new ArrayList<>();
            ArrayList<NumericValue> currentWeekValue = new ArrayList<>();
            ArrayList<Double> weights = new ArrayList<>();

            for (WeekdayMeetingIterable weekdayMeetingIterable : termWeekdayMeetingIterable) {
                double accumulator = 0;
                StringBuilder explanation = new StringBuilder();

                if (!termWeekdayMeetingIterable.getCurrentWeekSubContext().equals(currentWeek)) {
                    if (currentWeek != null) {
                        termValues.add(currentWeekValue);
                        currentWeekValue = new ArrayList<>();
                    }
                    currentWeek = termWeekdayMeetingIterable.getCurrentWeekSubContext();
                    weights.add(currentWeek.getWeight());
                }

                for (Meeting meeting : weekdayMeetingIterable) {
                    double inc = meetingAttribute.get(meeting);
                    accumulator += inc;
                    if (inc != 0) explanation.append(String.format("- %s = %.2f\n", meeting, inc));
                }
                explanation.append(String.format("= %.2f\n", accumulator));
                currentWeekValue.add((NumericValue) new NumericValue(accumulator).setExplanation(explanation));
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
