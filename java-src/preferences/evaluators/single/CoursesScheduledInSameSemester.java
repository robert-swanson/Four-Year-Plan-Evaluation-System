package preferences.evaluators.single;

import objects.misc.CourseID;
import objects.misc.TermYear;
import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.context.iterables.courseoffering.PlanCourseOfferingIterator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.result.Value;

import java.util.Set;

public class CoursesScheduledInSameSemester implements BooleanContextEvaluator {
    Set<CourseID> courseIDS;
    public CoursesScheduledInSameSemester(Set<CourseID> courseIDS) {
        this.courseIDS = courseIDS;
    }

    @Override
    public Result getValue(Context context) {
        PlanCourseOfferingIterator iterator = context.courseOfferingIterator();
        String description = "Checks to see that any of these courses are scheduled in the same semester";
        String explanation = "All classes scheduled in same semester: ";
        TermYear sharedTermYear = null;
        Value value = Value.TRUE;

        for (CourseOffering courseOffering : iterator) {
            if (courseIDS.contains(courseOffering.getCourse().getCourseID())) {
                if (sharedTermYear == null) {
                    sharedTermYear = iterator.getCurrentTermYear();
                } else if (sharedTermYear.equals(iterator.getCurrentTermYear())) {
                    explanation += String.format(" %s, ", sharedTermYear);
                } else {
                    explanation = String.format("Earlier classes were scheduled for %s, but %s was scheduled for %s", sharedTermYear, courseOffering.getCourse().getCourseID(), iterator.getCurrentTermYear());
                    return new PlanResult<>(description, explanation, Value.FALSE);
                }
            }
        }
        return new PlanResult<>(description, explanation, value);
    }

    @Override
    public String toString() {
        return String.format("%s scheduled in same semester", courseIDS);
    }
}