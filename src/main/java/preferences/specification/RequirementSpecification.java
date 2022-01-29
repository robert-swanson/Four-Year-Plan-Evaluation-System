package preferences.specification;

import preferences.constraints.RequireableConstraint;
import preferences.context.Context;
import preferences.explanation.specification.RequirementSpecificationResultExplanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;
import psl.PSLGenerator;

public class RequirementSpecification extends Specification {
    private final RequireableConstraint requireableConstraint;
    private final boolean invert;

    public RequirementSpecification(RequireableConstraint requireableConstraint, boolean invert) {
        this.requireableConstraint = requireableConstraint;
        this.invert = invert;
    }

    @Override
    public Score evaluate(Context context, boolean evaluateALl) {
        if ((invert) ^ (requireableConstraint.fulfilled(context))) {
            lastScore = Score.valid();
        } else {
            lastScore = Score.invalid();
        }
        return lastScore;
    }

    @Override
    public Specification getSimplifiedSpecification() {
        return this;
    }

    @Override
    public SpecificationResultExplanation explainLastResult() {
        return new RequirementSpecificationResultExplanation(this, requireableConstraint.explainLastResult());
    }

    @Override
    public String describe() {
        return toPSL().replaceAll("\n$","");
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("require %s%s.\n", invert ? "not " : "", requireableConstraint.toPSL()));
    }
}
