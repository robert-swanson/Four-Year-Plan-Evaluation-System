package preferences.evaluators.weekday;

import objects.misc.TimeRange;
import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.result.Result;
import preferences.result.Value;

public class MeetingInTimeRangeEvaluator extends WeekdayMeetingEvaluator implements BooleanContextEvaluator {
    private final TimeRange timeRange;

    public MeetingInTimeRangeEvaluator(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    @Override
    public Result getValue(Context context) {
        return getValue(context, String.format("Meeting in time range %s", timeRange), (meetings, weekday) -> {
            for (Meeting meeting : meetings) {
                if (timeRange.overlapsWith(meeting.getTimeRange())) {
                    return Value.TRUE;
                }
            }
            return Value.FALSE;
        });
    }


    @Override
    public String describe() {
        return "meeting in time range";
    }
}
