import api.PSLInstance;
import org.junit.jupiter.api.Test;

public class IntegrationTest {
    static PSLInstance pslInstance;

    PSLInstance getPslInstance() throws Exception {
        if (pslInstance == null) {
            pslInstance = new PSLInstance("json/taylor/catalog.json", "json/taylor/offerings.json");
            pslInstance.addDependencyFile("test-input/dependencies.psl");
        }
        return pslInstance;
    }

    @Test
    void fullTest() throws Exception {
        PSLInstance pslInstance = getPslInstance();
        pslInstance.loadPSLFile("test-input/full-test.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }

    @Test
    void myPreferences() throws Exception {
        PSLInstance pslInstance = getPslInstance();
        pslInstance.loadPSLFile("test-input/my-preferences.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }

    @Test
    void simple() throws Exception {
        PSLInstance pslInstance = getPslInstance();
        pslInstance.loadPSLFile("test-input/simple.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }

    @Test
    void test() throws Exception {
        PSLInstance pslInstance = new PSLInstance("json/taylor/catalog.json", "json/taylor/offerings.json");
        pslInstance.addDependencyFile("test-input/dependencies.psl");
        pslInstance.loadPSLFile("test-input/test.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }
}
