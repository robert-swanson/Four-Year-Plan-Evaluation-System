package preferences.evaluators.single;

import objects.misc.CourseID;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.TimeContextEvaluator;
import preferences.result.Result;
import preferences.value.TimeValue;
import psl.PSLGenerator;

public class CourseEndTimeEvaluator extends TimeContextEvaluator {
    CourseID courseID;

    public CourseEndTimeEvaluator(CourseID courseID) {
        this.courseID = courseID;
    }

    @Override
    public Result getValue(Context context) {
        String description = String.format("End time for first meeting described for first course offering found for %s", courseID);
        lastResult = SingleCourseOfferingEvaluator.getValue(context, description, courseOffering -> new TimeValue(courseOffering.getMeetings().getFirst().getEndTime()), courseID);
        return lastResult;
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
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("%s ending", courseID));
    }
}
