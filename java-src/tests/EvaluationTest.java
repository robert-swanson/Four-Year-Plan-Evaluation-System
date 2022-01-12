package tests;

import generation.RandomGenerator;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import objects.plan.Plan;
import preferences.context.Context;
import preferences.specification.FullSpecification;
import preferences.specification.Specification;
import psl.PSLCompiler;
import psl.exceptions.PSLCompileError;

import java.util.ArrayList;
import java.util.Collections;

public class EvaluationTest {
    public static void main(String[] args) {
        String[] paths = {
                "test-input/full-test.psl",
                "test-input/test.psl",
                "test-input/my-preferences.psl",
                "test-input/simple.psl",
        };

        try {
            PSLCompiler compiler = new PSLCompiler(paths[0], null);
            FullSpecification compiledSpecification = compiler.compile();
//            runNumCoursesTest(compiledSpecification);
//            runNumTermsTest(compiledSpecification);
            runNumOfferingsTest(compiledSpecification);
        } catch (PSLCompileError e) {
            e.printStackTrace();
        }
    }

    private static void runNumCoursesTest(Specification specification) {
        int minCourses = 1000, maxCourses = 100000, interval = 5000;
        RandomGenerator generator;

        for (int numCourses = minCourses; numCourses <= maxCourses; numCourses += interval) {
            generator = new RandomGenerator(1, numCourses, 8, 2);
            System.out.printf("%d, %.2f\n", numCourses, runTest(generator, specification));
        }
    }

    private static void runNumTermsTest(Specification specification) {
        int minTerms = 10, maxTerms = 200, interval = 20;
        RandomGenerator generator;

        for (int numTerms = minTerms; numTerms <= maxTerms; numTerms += interval) {
            generator = new RandomGenerator(1, 20, numTerms, 2);
            System.out.printf("%d, %.2f\n", numTerms, runTest(generator, specification));
        }
    }

    private static void runNumOfferingsTest(Specification specification) {
        int minOfferings = 1, maxOfferings = 20, interval = 1;
        RandomGenerator generator;

        for (int numOfferings = minOfferings; numOfferings <= maxOfferings; numOfferings += interval) {
            generator = new RandomGenerator(1, 20, 8, numOfferings);
            System.out.printf("%d, %.2f\n", numOfferings, runTest(generator, specification));
        }
    }

    public static double runTest(RandomGenerator generator, Specification specification) {
        int numAverage = 10;
        int reps = 120;
        int totalMills = 0;
        boolean evaluateAll = true;
        ArrayList<Integer> times = new ArrayList<>();

        for (int i = 0; i < reps ; i++) {
            Plan plan = generator.generateRandomPlan();
            Context context = new Context(plan);

            long startTime = System.nanoTime();
            specification.evaluate(context, evaluateAll);
            long endTime = System.nanoTime();

            if (reps-i <= 5) {
                int mills = (int)(endTime-startTime);
                totalMills += mills;
                times.add(mills);
            }
        }

        double mean = (double)totalMills / numAverage / 1000000.0;
        Collections.sort(times);
        double median = times.get(times.size()/2)/1000000.0;
        return median;
    }
}
