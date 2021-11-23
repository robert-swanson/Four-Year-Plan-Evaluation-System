package preferences.evaluators.general;

import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.Result;

public class MeetingMinutesEvaluator extends MeetingAccumulatorEvaluator implements ScalableContextEvaluator {
    @Override
    public Result getValue(Context context) {
        return getValue(context, "Total meeting time minutes", Meeting::getMeetingMinutes);
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 20; case terms, days -> 1; };
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 120 * 50; case terms -> 15*50; case days -> 3*50; };
    }

    @Override
    public String toString() {
        return "meeting minutes";
    }
}
