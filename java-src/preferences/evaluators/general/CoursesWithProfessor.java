package preferences.evaluators.general;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.result.Result;

public class CoursesWithProfessor extends CourseOfferingAccumulatorEvaluator {
    private String professor;

    public CoursesWithProfessor(String professor) {
        this.professor = professor;
    }

    @Override
    public Result getValue(Context context) {
        return getValue(context, String.format("Total courses with %s", professor), courseOffering -> {
            for (String thisProf : courseOffering.getProfessors()) {
                if (professor.equals(thisProf)) return 1;
            }
            return 0;
        });
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
    public String toString() {
        return "courses";
    }
}
