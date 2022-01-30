package preferences.evaluators.weekday;

import objects.misc.Time;
import objects.misc.TimeRange;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.Result;
import preferences.value.NumericValue;
import psl.PSLGenerator;

import java.util.ArrayList;
import java.util.Collections;

public class NumTimeBlocksEvaluator extends NumericContextEvaluator {
    ArrayList<TimeRange> reservedTimeBlocks;
    int maxBufferMinutes;

    public NumTimeBlocksEvaluator(int maxBufferMinutes, ArrayList<TimeRange> reservedTimeBlocks) {
        this.reservedTimeBlocks = reservedTimeBlocks;
        this.maxBufferMinutes = maxBufferMinutes;
    }

    @Override
    public Result getValue(Context context) {
        String description = String.format("Num time blocks with less than %d minutes between them%s", maxBufferMinutes, reservedTimeBlocks == null ? "" : String.format("(ignoring the following times ranges: %s)", reservedTimeBlocks));
        lastResult = WeekdayMeetingEvaluator.getValue(context, description, (meetings, weekday) -> {
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
            return new NumericValue(timeBlocks);
        });
        return lastResult;
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
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(generator.plural ? "time blocks" : "time block");
        if (!reservedTimeBlocks.isEmpty()) {
            String str = reservedTimeBlocks.toString();
            generator.addPSL(String.format(" reserving %s", str.substring(1, str.length()-1)));
        }
    }
}
