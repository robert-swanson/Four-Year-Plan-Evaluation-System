import api.instance.LocalInstance;
import org.junit.jupiter.api.Test;
import preferences.specification.FullSpecification;

public class PSLUnitTests {
    static LocalInstance appInstance;

    private final String TESTS_ASSETS = "src/test/testAssets/tests/";
    private final String GENERATED_ASSETS = "src/test/testAssets/generated/";
    private final String PSL_ASSETS = "assets/psl/";
    private final String JSON_ASSETS = "assets/json/";

    LocalInstance getLocalInstance() throws Exception {
        if (appInstance == null) {
            appInstance = new LocalInstance(JSON_ASSETS + "catalog.json", JSON_ASSETS + "offerings.json");
            appInstance.addDependencyFile(PSL_ASSETS + "dependencies.psl");
        }
        return appInstance;
    }

    private void runTest(String pslFileName) throws Exception {
        LocalInstance instance = getLocalInstance();
        System.out.println("+ Loading Original File");
        FullSpecification specification = instance.loadPSLFile(TESTS_ASSETS + pslFileName);

        System.out.println("+ Evaluating");
        instance.evaluatePlansFile(JSON_ASSETS + "myPlan.json", specification, true);

        System.out.println("+ Generating PSL");
        String generatedFilename = GENERATED_ASSETS + pslFileName;
        specification.writePSL(generatedFilename);

        System.out.println("+ Loading Generated PSL");
        instance.loadPSLFile(generatedFilename);
    }

    @Test
    void comments() throws Exception {
        runTest("comments.psl");
    }

    @Test
    void numeric_evaluators() throws Exception {
        runTest("numeric-evaluators.psl");
    }

    @Test
    void term_year_evaluators() throws Exception {
        runTest("term-year-evaluators.psl");
    }

    @Test
    void time_evaluators() throws Exception {
        runTest("time-evaluators.psl");
    }

    @Test
    void boolean_evaluators() throws Exception {
        runTest("boolean-evaluators.psl");
    }

    @Test
    void requirements() throws Exception {
        runTest("requirements.psl");
    }

    @Test
    void preferences() throws Exception {
        runTest("preferences.psl");
    }

    @Test
    void specification_list() throws Exception {
        runTest("specification-list.psl");
    }

    @Test
    void conditional_specification() throws Exception {
        runTest("conditional-specification.psl");
    }

    @Test
    void contextual_specification() throws Exception {
        runTest("contextual-specification.psl");
    }

    @Test
    void conditions() throws Exception {
        runTest("conditions.psl");
    }

    @Test
    void constraints() throws Exception {
        runTest("constraints.psl");
    }
}
