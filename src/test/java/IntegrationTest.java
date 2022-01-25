import api.instance.LocalInstance;
import org.junit.jupiter.api.Test;
import preferences.specification.FullSpecification;

public class IntegrationTest {
    static LocalInstance appInstance;

    LocalInstance getLocalInstance() throws Exception {
        if (appInstance == null) {
            appInstance = new LocalInstance("assets/json/catalog.json", "assets/json/offerings.json");
            appInstance.addDependencyFile("assets/psl/dependencies.psl");
        }
        return appInstance;
    }

    @Test
    void fullTest() throws Exception {
        LocalInstance instance = getLocalInstance();
        FullSpecification specification = instance.loadPSLFile("assets/psl/full-test.psl");
        instance.evaluatePlansFile("assets/json/myPlan.json", specification, true);
    }

    @Test
    void myPreferences() throws Exception {
        LocalInstance instance = getLocalInstance();
        FullSpecification specification = instance.loadPSLFile("assets/psl/my-preferences.psl");
        instance.evaluatePlansFile("assets/json/myPlan.json", specification, true);
    }

    @Test
    void simple() throws Exception {
        LocalInstance instance = getLocalInstance();
        FullSpecification specification = instance.loadPSLFile("assets/psl/simple.psl");
        instance.evaluatePlansFile("assets/json/myPlan.json", specification, true);
    }

    @Test
    void test() throws Exception {
        LocalInstance instance = getLocalInstance();
        FullSpecification specification = instance.loadPSLFile("assets/psl/test.psl");
        instance.evaluatePlansFile("assets/json/myPlan.json", specification, true);
    }
}
