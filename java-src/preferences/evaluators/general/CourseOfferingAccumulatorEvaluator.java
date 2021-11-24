package preferences.evaluators.general;

import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.evaluators.ContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.context.iterables.courseoffering.PlanTermCourseOfferingIterable;
import preferences.context.iterables.courseoffering.TermCourseOfferingIterable;
import preferences.result.*;

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

    static PlanResult<ScalableValue.Numeric> getPlanValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        int accumulator = 0;
        StringBuilder explanation = new StringBuilder();
        for (CourseOffering courseOffering : context.courseOfferingIterator()) {
            accumulator += courseOfferingAttribute.get(courseOffering);
            explanation.append(String.format("  class \"%s\"\n", courseOffering));
        }
        return new PlanResult<>(description, explanation.toString(), new ScalableValue.Numeric(accumulator));
    }

    static TermsResult<ScalableValue.Numeric> getTermsValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        TermsResult<ScalableValue.Numeric> result = new TermsResult<>(description);
        StringBuilder explanation = new StringBuilder();
        PlanTermCourseOfferingIterable iterable = context.termCourseOfferingIterable();
        for (TermCourseOfferingIterable termCourseOfferingIterable : iterable) {
            explanation.append(String.format("    %s:\n", iterable.getTermYear().toString()));
            double accumulator = 0;
            for (CourseOffering courseOffering : termCourseOfferingIterable) {
                accumulator += courseOfferingAttribute.get(courseOffering);
                explanation.append(String.format("      %s\n", courseOffering));
            }
            result.addValue(new ScalableValue.Numeric(accumulator));
            explanation.append(String.format("      Term Count: %.2f\n", accumulator));
        }
        result.setResultExplanation(explanation.toString());
        return result;
    }

    private static DaysResult<ScalableValue.Numeric> getDaysValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        return MeetingAccumulatorEvaluator.getDaysValue(context, description, meeting -> courseOfferingAttribute.get(meeting.getCourseOffering()));
    }
}
