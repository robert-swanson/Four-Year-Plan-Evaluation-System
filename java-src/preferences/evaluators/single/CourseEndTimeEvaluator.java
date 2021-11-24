package preferences.evaluators.single;

import objects.misc.CourseID;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.result.Result;
import preferences.result.ScalableValue;

public class CourseEndTimeEvaluator extends SingleCourseOfferingEvaluator implements ScalableContextEvaluator {
    public CourseEndTimeEvaluator(CourseID courseID) {
        super(courseID);
    }

    @Override
    public Result getValue(Context context) {
        String description = String.format("End time for first meeting described for first course offering found for %s", super.courseID);
        lastValue = getValue(context, description, courseOffering -> new ScalableValue.TimeValue(courseOffering.getMeetings().getFirst().getEndTime()));
        return lastValue;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 90;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 12*60;
    }

    @Override
    public String describe() {
        return "course end time";
    }
}
