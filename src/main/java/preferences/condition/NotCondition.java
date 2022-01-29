package preferences.condition;

import preferences.context.Context;
import preferences.explanation.Explanation;
import preferences.explanation.specification.ConditionResultExplanation;
import psl.PSLGenerator;

public class NotCondition extends Condition {
    private final Condition condition;

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public boolean evaluate(Context context) {
        return !condition.evaluate(context);
    }


    @Override
    public Explanation explainLastResult() {
        return new ConditionResultExplanation.NotExplanation(this, condition.explainLastResult());
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("not %s", condition.toPSL()));
    }
}
