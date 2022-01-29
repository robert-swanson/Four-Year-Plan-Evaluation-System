package preferences.condition;

import preferences.context.Context;
import preferences.explanation.Explanation;
import preferences.explanation.specification.ConditionResultExplanation;
import psl.PSLGenerator;

public class AndCondition extends Condition {
    private final Condition left;
    private final Condition right;

    public AndCondition(Condition left, Condition right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean evaluate(Context context) {
        return left.evaluate(context) && right.evaluate(context);
    }


    @Override
    public Explanation explainLastResult() {
        return new ConditionResultExplanation.AndExplanation(this, left.explainLastResult(), right.explainLastResult());
    }

    @Override
    public String describe() {
        return toPSL();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("(%s and %s)", left.toPSL(), right.toPSL()));
    }
}
