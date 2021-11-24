package preferences.evaluators.single;

import objects.misc.CourseID;
import objects.misc.TermYear;
import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.context.iterables.courseoffering.PlanCourseOfferingIterator;
import preferences.evaluators.ContextEvaluator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.result.Value;

public class CourseBeforeCourseEvaluator extends BooleanContextEvaluator {
    final private CourseID first, second;
    public CourseBeforeCourseEvaluator(CourseID first, CourseID second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public Result getValue(Context context) {
        PlanCourseOfferingIterator iterator = context.courseOfferingIterator();
        String description = "Checks to ensures that if the second course if scheduled, its after the first course";
        String explanation;
        TermYear firstTermYear = null, secondTearmYear = null;
        Value value;

        for (CourseOffering courseOffering : iterator) {
            if (firstTermYear == null && first.equals(courseOffering.getCourse().getCourseID())) {
                firstTermYear = iterator.getCurrentTermYear();
                if (secondTearmYear != null) break;
            } else if (secondTearmYear == null && second.equals(courseOffering.getCourse().getCourseID())) {
                secondTearmYear = iterator.getCurrentTermYear();
                if (firstTermYear != null) break;
            }
        }

        value = Value.TRUE;
        if (firstTermYear == null && second == null) {
            explanation = "Neither course is scheduled";
        } else if (firstTermYear == null) {
            value = Value.FALSE;
            explanation = String.format("Prerequisite: %s is not scheduled", first);
        } else if (secondTearmYear == null) {
            explanation = String.format("Post-requisite: %s is not scheduled", second);
        } else if (firstTermYear.compareTo(secondTearmYear) > 0) {
            explanation = String.format("Prerequisite (%s: %s) is not scheduled before Post-requisite (%s: %s)", first, firstTermYear, second, secondTearmYear);
            value = Value.FALSE;
        } else {
            explanation = String.format("Prerequisite (%s: %s) is scheduled before Post-requisite (%s: %s)", first, firstTermYear, second, secondTearmYear);
        }
        lastValue = new PlanResult<>(description, explanation, value);
        return lastValue;
    }

    @Override
    public String describe() {
        return String.format("course %s before %s", first, second);
    }
}
