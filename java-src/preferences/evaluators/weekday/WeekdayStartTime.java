package preferences.evaluators.weekday;

import objects.misc.Time;
import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.Result;
import preferences.result.ScalableValue;

public class WeekdayStartTime extends ScalableContextEvaluator {

    @Override
    public Result getValue(Context context) {
        return WeekdayMeetingEvaluator.getValue(context, "Weekday Start Time", (meetings, weekday) -> {
            Time start = null;
            for (Meeting meeting: meetings) {
                Time meetingStart = meeting.getStartTime();
                if (start == null || meetingStart.compareTo(start) < 0) {
                    start = meetingStart;
                }
            }
            return new ScalableValue.TimeValue(start);
        });
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 60;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 60*9;
    }

    @Override
    public String describe() {
        return "day start time";
    }
}
