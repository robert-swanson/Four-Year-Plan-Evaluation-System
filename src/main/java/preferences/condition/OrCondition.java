package preferences.condition;

import preferences.context.Context;
import preferences.explanation.Explanation;
import preferences.explanation.specification.ConditionResultExplanation;
import psl.PSLGenerator;

public class OrCondition extends Condition {
    private final Condition left;
    private final Condition right;

    public OrCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Context context) {
        return left.evaluate(context) || right.evaluate(context);
    }

    @Override
    public Explanation explainLastResult() {
        return new ConditionResultExplanation.OrExplanation(this, left.explainLastResult(), right.explainLastResult());
    }
    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("(%s or %s)", left.toPSL(), right.toPSL()));
    }
}
