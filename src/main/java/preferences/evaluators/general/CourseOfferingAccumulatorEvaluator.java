package preferences.evaluators.general;

import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.context.iterables.courseoffering.PlanTermCourseOfferingIterable;
import preferences.context.iterables.courseoffering.TermCourseOfferingIterable;
import preferences.result.*;
import preferences.value.NumericValue;

public abstract class CourseOfferingAccumulatorEvaluator extends ScalableContextEvaluator {
    public interface CourseOfferingAttribute {
        double get(CourseOffering courseOffering);
    }

    static Result getValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        return switch (context.getContextLevel()) {
            case fullPlan -> getPlanValue(context, description + " for plan", courseOfferingAttribute);
            case terms -> getTermsValue(context, description + " for terms in context", courseOfferingAttribute);
            case days -> getDaysValue(context, description + " for weekdays in context", courseOfferingAttribute);
        };
    }

    static PlanResult<NumericValue> getPlanValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        int accumulator = 0;
        StringBuilder explanation = new StringBuilder();
        for (CourseOffering courseOffering : context.courseOfferingIterator()) {
            accumulator += courseOfferingAttribute.get(courseOffering);
            explanation.append(String.format("  class \"%s\"\n", courseOffering));
        }
        return new PlanResult<>(new NumericValue(accumulator));
    }

    static TermsResult<NumericValue> getTermsValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        TermsResult<NumericValue> result = new TermsResult<>(description);
        StringBuilder explanation = new StringBuilder();
        PlanTermCourseOfferingIterable iterable = context.termCourseOfferingIterable();
        for (TermCourseOfferingIterable termCourseOfferingIterable : iterable) {
            explanation.append(String.format("    %s:\n", iterable.getTermYear().toString()));
            double accumulator = 0;
            for (CourseOffering courseOffering : termCourseOfferingIterable) {
                accumulator += courseOfferingAttribute.get(courseOffering);
                explanation.append(String.format("      %s\n", courseOffering));
            }
            result.addValue(new NumericValue(accumulator));
            explanation.append(String.format("      Term Count: %.2f\n", accumulator));
        }
        return result;
    }

    private static DaysResult<NumericValue> getDaysValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        return MeetingAccumulatorEvaluator.getDaysValue(context, description, meeting -> courseOfferingAttribute.get(meeting.getCourseOffering()));
    }
}
