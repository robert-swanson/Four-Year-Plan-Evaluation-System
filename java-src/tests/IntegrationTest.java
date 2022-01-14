package tests;

import api.PSLInstance;

public class IntegrationTest {
    static PSLInstance pslInstance;

    PSLInstance getPslInstance() throws Exception {
        if (pslInstance == null) {
            pslInstance = new PSLInstance("json/taylor/catalog.json", "json/taylor/offerings.json");
            pslInstance.addDependencyFile("test-input/dependencies.psl");
        }
        return pslInstance;
    }

    @org.junit.jupiter.api.Test
    void fullTest() throws Exception {
        PSLInstance pslInstance = getPslInstance();
        pslInstance.loadPSLFile("test-input/full-test.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }

    @org.junit.jupiter.api.Test
    void myPreferences() throws Exception {
        PSLInstance pslInstance = getPslInstance();
        pslInstance.loadPSLFile("test-input/my-preferences.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }

    @org.junit.jupiter.api.Test
    void simple() throws Exception {
        PSLInstance pslInstance = getPslInstance();
        pslInstance.loadPSLFile("test-input/simple.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }

    @org.junit.jupiter.api.Test
    void test() throws Exception {
        PSLInstance pslInstance = new PSLInstance("json/taylor/catalog.json", "json/taylor/offerings.json");
        pslInstance.addDependencyFile("test-input/dependencies.psl");
        pslInstance.loadPSLFile("test-input/test.psl");
        pslInstance.evaluatePlanFile("json/taylor/myPlan.json");
    }
}
