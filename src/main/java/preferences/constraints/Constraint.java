package preferences.constraints;

import preferences.context.Context;
import preferences.context.ContextLevel;
import preferences.evaluators.ContextEvaluator;
import preferences.explanation.Explainable;
import preferences.explanation.constraints.ConstraintResultExplanation;
import psl.PSLComponent;
import psl.PSLGenerator;

public abstract class Constraint implements Explainable, PSLComponent {
    public enum ConstraintType { equal, lessThan, greaterThan, lessThanOrEqual, greaterThanOrEqual, booleanConstraint, more, less }
    public final ConstraintType constraintType;

    protected ContextEvaluator contextEvaluator;
    protected ContextLevel contextLevel;

    public Constraint(ContextEvaluator contextEvaluator, ConstraintType constraintType, ContextLevel contextLevel) {
        this.contextEvaluator = contextEvaluator;
        this.constraintType = constraintType;
        this.contextLevel = contextLevel;
    }

    public abstract double score(Context context);
    public ConstraintResultExplanation explainLastResult() {
        return new ConstraintResultExplanation(this, contextEvaluator);
    }

    @Override
    public String describe() {
        return toPSL();
    }

    @Override
    public String toString() {
        return toPSL();
    }

    protected void generate(PSLGenerator generator, String first, PSLComponent second, PSLComponent third) {
        if (first.isBlank()) {
            generator.addPSL(String.format("%s %s", second.toPSL(), third.toPSL()));
        } else if (third == null) {
            generator.addPSL(String.format("%s %s", first, second.toPSL()));
        } else {
            generator.addPSL(String.format("%s %s %s", first, second.toPSL(), third.toPSL()));
        }
    }

    protected void generate(PSLGenerator generator, PSLComponent first, String second, PSLComponent third) {
        if (third == null) {
            generator.addPSL(String.format("%s %s", first.toPSL(), second));
        } else {
            generator.addPSL(String.format("%s %s %s", first.toPSL(), second, third.toPSL()));
        }
    }

    protected void generate(PSLGenerator generator, PSLComponent first, PSLComponent second, String third) {
        generator.addPSL(String.format("%s %s %s", first.toPSL(), second.toPSL(), third));
    }
}
