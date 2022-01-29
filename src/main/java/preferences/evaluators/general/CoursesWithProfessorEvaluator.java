package preferences.evaluators.general;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.NumericContextEvaluator;
import preferences.result.Result;
import psl.PSLGenerator;

public class CoursesWithProfessorEvaluator extends NumericContextEvaluator {
    private String professor;

    public CoursesWithProfessorEvaluator(String professor) {
        this.professor = professor;
    }

    @Override
    public Result getValue(Context context) {
        lastResult = CourseOfferingAccumulatorEvaluator.getValue(context, String.format("Total courses with %s", professor), courseOffering -> {
            for (String thisProf : courseOffering.getProfessors()) {
                if (professor.equals(thisProf)) {
                    return 1;
                }
            }
            return 0;
        });
        return lastResult;
    }

    @Override
    public double getDeviance(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 3; case terms, days -> 1; };
    }

    @Override
    public double getAverage(ContextLevel contextLevel) {
        return switch (contextLevel) { case fullPlan -> 3; case terms, days -> 1; };
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("courses with \"%s\"", professor));
    }
}
