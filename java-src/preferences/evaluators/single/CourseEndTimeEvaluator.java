package preferences.evaluators.single;

import objects.misc.CourseID;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.Result;
import preferences.result.ScalableValue;

public class CourseEndTimeEvaluator extends SingleCourseOfferingEvaluator {
    public CourseEndTimeEvaluator(CourseID courseID) {
        super(courseID);
    }

    @Override
    public Result getValue(Context context) {
        String description = String.format("End time for first meeting described for first course offering found for %s", super.courseID);
        return getValue(context, description, courseOffering -> new ScalableValue.TimeValue(courseOffering.getMeetings().getFirst().getEndTime()));
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
    public String toString() {
        return "course end time";
    }
}