import generation.RandomGenerator;
import objects.misc.CourseID;
import preferences.constraints.BooleanConstraint;
import preferences.constraints.GreaterThanOrEqualConstraint;
import preferences.context.Condition;
import preferences.context.ContextLevel;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.evaluators.general.TotalCreditsEvaluator;
import preferences.evaluators.single.CourseTermYearEvaluator;
import preferences.result.ScalableValue;
import preferences.specification.*;

public class SpecificationTests {
    public static void main(String[] args) {
        RandomGenerator generator = new RandomGenerator(5, 100, 8, 3);

//        testNumRequireCourse(generator);
//        testNumContextFilters(generator);
        testContextFiltersDepth(generator);
    }

    private enum EvaluatorType {
        Courses, CoursesFromList, Credits, CreditsFromList, UpperCourses, UpperCredits, MeetingMinutes, CoursesWithProf
    }


    private static void testNumRequireCourse(RandomGenerator generator) {
        int numRequirements = 1000;
        SpecificationList specificationList = new SpecificationList();

        for (int i = 0; i < numRequirements; i++) {
            CourseID courseID = generator.getRandomCourse();
            TermYearContextEvaluator termYearContextEvaluator = new CourseTermYearEvaluator(courseID);
            BooleanConstraint booleanConstraint = new BooleanConstraint(termYearContextEvaluator, ContextLevel.fullPlan);
            RequirementSpecification requirementSpecification = new RequirementSpecification(booleanConstraint, false);
//            PreferenceSpecification preferenceSpecification = new PreferenceSpecification(booleanConstraint, 1.0, false);
            specificationList.addSpecification(requirementSpecification);

            if (i % 10 == 0) {
                double mills = runTest(specificationList, generator);
                System.out.printf("NumRequireCourses,%d,%.2f\n", i+1, mills);
            }
        }
    }

    private static void testNumContextFilters(RandomGenerator generator) {
        int numFilters = 1000;
        SpecificationList specificationList = new SpecificationList();

        for (int i = 0; i < numFilters; i++) {
            CourseID courseID = generator.getRandomCourse();
            TermYearContextEvaluator termYearContextEvaluator = new CourseTermYearEvaluator(courseID);
            BooleanConstraint booleanConstraint = new BooleanConstraint(termYearContextEvaluator, ContextLevel.fullPlan);
            RequirementSpecification requirementSpecification = new RequirementSpecification(booleanConstraint, false);

            TotalCreditsEvaluator creditsEvaluator = new TotalCreditsEvaluator();
            GreaterThanOrEqualConstraint constraint = new GreaterThanOrEqualConstraint(creditsEvaluator, new ScalableValue.Numeric(3), ContextLevel.days);
            Condition condition = new Condition.ConstraintCondition(constraint);

            ContextualSpecification contextualSpecification = new ContextualSpecification(requirementSpecification, ContextLevel.days, condition, null, null);
            specificationList.addSpecification(contextualSpecification);

            if (i % 10 == 0) {
                double mills = runTest(specificationList, generator);
                System.out.printf("ContextualSpecification,%d,%.2f\n", i+1, mills);
            }
        }
    }

    private static void testContextFiltersDepth(RandomGenerator generator) {
        int numFilters = 1000;

        CourseID courseID = generator.getRandomCourse();
        TermYearContextEvaluator termYearContextEvaluator = new CourseTermYearEvaluator(courseID);
        BooleanConstraint booleanConstraint = new BooleanConstraint(termYearContextEvaluator, ContextLevel.fullPlan);
        Specification lastSpecification = new RequirementSpecification(booleanConstraint, false);

        for (int i = 0; i < numFilters; i++) {
            TotalCreditsEvaluator creditsEvaluator = new TotalCreditsEvaluator();
            GreaterThanOrEqualConstraint constraint = new GreaterThanOrEqualConstraint(creditsEvaluator, new ScalableValue.Numeric(3), ContextLevel.days);
            Condition condition = new Condition.ConstraintCondition(constraint);

            lastSpecification = new ContextualSpecification(lastSpecification, ContextLevel.days, condition, null, null);

            if (i % 10 == 0) {
                double mills = runTest(lastSpecification, generator);
                System.out.printf("ContextualSpecification,%d,%.2f\n", i+1, mills);
            }
        }
    }

    private static double runTest(Specification specification, RandomGenerator generator) {
        return EvaluationTest.runTest(generator, specification);
    }
}
