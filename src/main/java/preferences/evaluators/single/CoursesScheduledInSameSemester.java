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

public class CoursesScheduledInSameSemester extends BooleanContextEvaluator {
    Set<CourseID> courseIDS;
    public CoursesScheduledInSameSemester(Set<CourseID> courseIDS) {
        this.courseIDS = courseIDS;
    }

    @Override
    public Result getValue(Context context) {
        PlanCourseOfferingIterator iterator = context.courseOfferingIterator();
        String description = "Checks to see that any of these courses are scheduled in the same semester";
        StringBuilder explanation = new StringBuilder("All classes scheduled in same semester: ");
        TermYear sharedTermYear = null;
        Value value = Value.TRUE;

        for (CourseOffering courseOffering : iterator) {
            if (courseIDS.contains(courseOffering.getCourse().getCourseID())) {
                if (sharedTermYear == null) {
                    sharedTermYear = iterator.getCurrentTermYear();
                } else if (sharedTermYear.equals(iterator.getCurrentTermYear())) {
                    explanation.append(String.format(" %s, ", sharedTermYear));
                } else {
                    explanation = new StringBuilder(String.format("Earlier classes were scheduled for %s, but %s was scheduled for %s", sharedTermYear, courseOffering.getCourse().getCourseID(), iterator.getCurrentTermYear()));
                    lastResult = new PlanResult<>(Value.FALSE);
                }
            }
        }
        lastResult = new PlanResult<>(value);
        return lastResult;
    }

    @Override
    public String describe() {
        return String.format("%s scheduled in same semester", courseIDS);
    }
}
