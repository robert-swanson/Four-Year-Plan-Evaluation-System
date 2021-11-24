package preferences.evaluators.general;

import objects.misc.CourseID;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.Result;

import java.util.Set;

public class TotalCreditsFromSetEvaluator extends CourseOfferingAccumulatorEvaluator {
    Set<CourseID> courseIDSet;

    public TotalCreditsFromSetEvaluator(Set<CourseID> courseSet) {
        courseIDSet = courseSet;
    }

    @Override
    public Result getValue(Context context) {
        lastValue = getValue(context, String.format("Total credits from set %s", courseIDSet.toString()), courseOffering -> courseIDSet.contains(courseOffering.getCourse().getCourseID()) ? courseOffering.getCredits() : 0);
        return lastValue;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return courseIDSet.size() * 3.0 / 4.0;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return courseIDSet.size()/2.0;
    }

    @Override
    public String describe() {
        return String.format("credits from %s", courseIDSet.toString());
    }
}
