import generation.RandomGenerator;
import objects.plan.Plan;
import preferences.context.Context;
import preferences.specification.FullSpecification;
import psl.PSLCompiler;
import psl.exceptions.PSLCompileError;

public class EvaluationRepsTest {
    public static void main(String[] args) {
        String[] paths = {
                "assets/psl/full-grammar.psl",
                "assets/psl/test.psl",
                "assets/psl/my-preferences.psl",
                "assets/psl/simple.psl",
        };
        for (String path :  paths) {
            runRepeatEvaluationTest(path);
        }
    }

    private static void runRepeatEvaluationTest(String pslFilePath) {
        int reps = 1000;
        boolean evaluateAll = true;

        try {
            PSLCompiler compiler = new PSLCompiler();
            FullSpecification compiledSpecification = compiler.compileFile(pslFilePath);
            RandomGenerator generator = new RandomGenerator(1, 20, 8, 2);

            for (int i = 0; i < reps; i++) {
                Plan plan = generator.generateRandomPlan();
                Context context = new Context(plan);

                long startTime = System.nanoTime();
                compiledSpecification.evaluate(context, evaluateAll);
                long endTime = System.nanoTime();

                int mills = (int)(endTime-startTime);
                System.out.printf("%s, %d, %.2f\n", pslFilePath, i, mills/1000000.0);
            }
        } catch (PSLCompileError e) {
            e.printStackTrace();
        }
    }
}
