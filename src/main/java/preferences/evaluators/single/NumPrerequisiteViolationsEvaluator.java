package preferences.evaluators.single;

import objects.catalog.Course;
import objects.misc.CourseID;
import objects.misc.TermYear;
import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.context.iterables.courseoffering.PlanCourseOfferingIterator;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.value.NumericValue;
import preferences.value.Value;
import psl.PSLGenerator;

public class NumPrerequisiteViolationsEvaluator extends NumericContextEvaluator {

    @Override
    public Result getValue(Context context) {
        PlanCourseOfferingIterator iterator = context.courseOfferingIterator();
        String description = "Counts the number of prerequistie violations in the entire plan (regardless of context)";
        StringBuilder explanation = new StringBuilder();

        int violations = 0;
        var map = context.getPlan().getCourseMap();
        for (CourseID courseID: map.keySet()) {
            CourseOffering courseOffering = map.get(courseID).get(0);
            Course course = courseOffering.getCourse();
            if (course.prerequisites == null) continue;
            TermYear takingB = courseOffering.getTermYear();
            for (CourseID prereq : course.prerequisites) {
                CourseOffering offering = context.getPlan().getOffering(prereq);
                if (offering == null) {
                    explanation.append(String.format("- '%s' (scheduled for %s) requires '%s' (not scheduled)\n", courseID, takingB, prereq));
                    violations++;
                } else {
                    TermYear takingA = offering.getTermYear();
                    if (takingA.compareTo(takingB) >= 0) {
                        explanation.append(String.format("- '%s' (scheduled for %s) requires '%s' (scheduled too late for %s)\n", courseID, takingB, prereq, takingA));
                        violations++;
                    }
                }
            }
        }

        Value value = new NumericValue(violations);
        value.setExplanation(explanation);
        lastResult = new PlanResult<>(value);
        return lastResult;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return 3;
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return 0;
    }


    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("prerequisite violations");
    }
}
