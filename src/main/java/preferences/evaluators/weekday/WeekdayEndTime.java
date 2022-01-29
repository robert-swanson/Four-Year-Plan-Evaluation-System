package preferences.evaluators.weekday;

import objects.misc.Time;
import objects.offerings.Meeting;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.TimeContextEvaluator;
import preferences.result.Result;
import preferences.value.TimeValue;
import psl.PSLGenerator;

public class WeekdayEndTime extends TimeContextEvaluator {

    @Override
    public Result getValue(Context context) {
        lastResult = WeekdayMeetingEvaluator.getValue(context, "Weekday End Time", (meetings, weekday) -> {
            Time end = null;
            for (Meeting meeting: meetings) {
                Time meetingEnd = meeting.getEndTime();
                if (end == null || meetingEnd.compareTo(end) > 0) {
                    end = meetingEnd;
                }
            }
            return new TimeValue(end);
        });
        return lastResult;
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
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("ending");
    }
}
