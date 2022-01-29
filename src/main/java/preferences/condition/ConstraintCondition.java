package preferences.condition;

import preferences.constraints.RequireableConstraint;
import preferences.context.Context;
import preferences.explanation.Explanation;
import psl.PSLGenerator;

public class ConstraintCondition extends Condition {
    RequireableConstraint requireableConstraint;

    public ConstraintCondition(RequireableConstraint requireableConstraint) {
        this.requireableConstraint = requireableConstraint;
    }

    @Override
    public boolean evaluate(Context context) {
        return requireableConstraint.fulfilled(context);
    }


    @Override
    public Explanation explainLastResult() {
        return requireableConstraint.explainLastResult();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        requireableConstraint.generatePSL(generator);
    }
}
