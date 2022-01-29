package preferences.evaluators.weekday;

import objects.misc.TimeRange;
import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.result.Result;
import preferences.value.Value;
import psl.PSLGenerator;

public class MeetingInTimeRangeEvaluator extends BooleanContextEvaluator {
    private final TimeRange timeRange;

    public MeetingInTimeRangeEvaluator(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    @Override
    public Result getValue(Context context) {
        lastResult = WeekdayMeetingEvaluator.getValue(context, String.format("Meeting in time range %s", timeRange), (meetings, weekday) -> {
            for (Meeting meeting : meetings) {
                if (timeRange.overlapsWith(meeting.getTimeRange())) {
                    return Value.TRUE;
                }
            }
            return Value.FALSE;
        });
        return lastResult;
    }


    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("meeting at %s", timeRange.toString()));
    }
}
