package preferences.evaluators.general;

import objects.offerings.CourseOffering;
import preferences.context.Context;
import preferences.context.iterables.courseoffering.PlanTermCourseOfferingIterable;
import preferences.context.iterables.courseoffering.TermCourseOfferingIterable;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.DaysResult;
import preferences.result.PlanResult;
import preferences.result.Result;
import preferences.result.TermsResult;
import preferences.value.NumericValue;

public abstract class CourseOfferingAccumulatorEvaluator extends NumericContextEvaluator {
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
        double accumulator = 0;
        StringBuilder explanation = new StringBuilder();
        for (CourseOffering courseOffering : context.courseOfferingIterator()) {
            double inc = courseOfferingAttribute.get(courseOffering);
            accumulator += inc;
            if (inc != 0) explanation.append(String.format("- %s = %.2f\n", courseOffering, inc));
        }
        explanation.append(String.format("= %.2f\n", accumulator));
        return new PlanResult<>(new NumericValue(accumulator).setExplanation(explanation));
    }

    static TermsResult<NumericValue> getTermsValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        TermsResult<NumericValue> result = new TermsResult<>(description);
        PlanTermCourseOfferingIterable iterable = context.termCourseOfferingIterable();
        for (TermCourseOfferingIterable termCourseOfferingIterable : iterable) {
            StringBuilder explanation = new StringBuilder();
            double accumulator = 0;
            for (CourseOffering courseOffering : termCourseOfferingIterable) {
                double inc = courseOfferingAttribute.get(courseOffering);
                accumulator += inc;
                if (inc != 0) explanation.append(String.format("- %s = %.2f\n", courseOffering, inc));
            }
            explanation.append(String.format("= %.2f\n", accumulator));
            result.addValue((NumericValue) new NumericValue(accumulator).setExplanation(explanation));
        }
        return result;
    }

    private static DaysResult<NumericValue> getDaysValue(Context context, String description, CourseOfferingAttribute courseOfferingAttribute) {
        return MeetingAccumulatorEvaluator.getDaysValue(context, description, meeting -> courseOfferingAttribute.get(meeting.getCourseOffering()));
    }
}
