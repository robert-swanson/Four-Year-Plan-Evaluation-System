package preferences.evaluators.weekday;

import objects.misc.Time;
import objects.misc.TimeRange;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.Result;
import preferences.result.ScalableValue;

import java.util.ArrayList;
import java.util.Collections;

public class NumTimeBlocksEvaluator extends ScalableContextEvaluator {
    ArrayList<TimeRange> reservedTimeBlocks;
    int maxBufferMinutes;

    public NumTimeBlocksEvaluator(int maxBufferMinutes, ArrayList<TimeRange> reservedTimeBlocks) {
        this.reservedTimeBlocks = reservedTimeBlocks;
        this.maxBufferMinutes = maxBufferMinutes;
    }

    @Override
    public Result getValue(Context context) {
        String description = String.format("Num time blocks with less than %d minutes between them%s", maxBufferMinutes, reservedTimeBlocks == null ? "" : String.format("(ignoring the following times ranges: %s)", reservedTimeBlocks));
        lastValue = WeekdayMeetingEvaluator.getValue(context, description, (meetings, weekday) -> {
            int timeBlocks = 0;
            ArrayList<TimeRange> timeRanges = new ArrayList<>();
            meetings.forEach(meeting -> timeRanges.add(meeting.getTimeRange()));
            timeRanges.addAll(reservedTimeBlocks);
            Collections.sort(timeRanges);

            Time latestEndTime = null;
            for (TimeRange timeRange : timeRanges) {
                Time end = timeRange.end;
                if (latestEndTime == null || latestEndTime.minutesUntil(end) > maxBufferMinutes)  {
                    timeBlocks++;
                }
                if (latestEndTime == null || latestEndTime.isBeforeOrEqual(end)) {
                    latestEndTime = end;
                }
            }
            return new ScalableValue.Numeric(timeBlocks);
        });
        return lastValue;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 2;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 3;
    }

    @Override
    public String describe() {
        return "time blocks";
    }
}
