package preferences.condition;

import preferences.context.Context;
import preferences.explanation.Explanation;
import psl.PSLGenerator;

class TrueCondition extends Condition {
    TrueCondition() {
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TrueCondition;
    }

    @Override
    public boolean evaluate(Context context) {
        return true;
    }

    @Override
    public String describe() {
        return "True";
    }

    @Override
    public Explanation explainLastResult() {
        return null;
    }

    @Override
    public String toString() {
        return "TRUE";
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL("TRUE");
    }
}
