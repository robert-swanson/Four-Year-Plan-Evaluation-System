package preferences.evaluators.weekday;

import objects.misc.Time;
import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.Result;
import preferences.result.ScalableValue;

public class WeekdayEndTime extends WeekdayMeetingEvaluator implements ScalableContextEvaluator {

    @Override
    public Result getValue(Context context) {
        return getValue(context, "Weekday End Time", (meetings, weekday) -> {
            Time end = null;
            for (Meeting meeting: meetings) {
                Time meetingEnd = meeting.getEndTime();
                if (end == null || meetingEnd.compareTo(end) > 0) {
                    end = meetingEnd;
                }
            }
            return new ScalableValue.TimeValue(end);
        });
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 60;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 15*60;
    }

    @Override
    public String toString() {
        return "day end time";
    }
}
