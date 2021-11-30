package preferences.specification;

import preferences.constraints.Constraint;
import preferences.context.Context;
import preferences.explanation.specification.PreferenceSpecificationResultExplanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;

public class PreferenceSpecification extends Specification {
    private final Constraint constraint;
    private final double weight;
    private final boolean invert;

    public PreferenceSpecification(Constraint constraint, double weight, boolean invert){
        this.constraint = constraint;
        this.weight = weight;
        this.invert = invert;
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        lastScore = new Score(constraint.score(context), weight, invert);
        return lastScore;
    }

    @Override
    public Specification getSimplifiedSpecification() {
        return this;
    }

    @Override
    public SpecificationResultExplanation explainLastResult() {
        return new PreferenceSpecificationResultExplanation(this, constraint.explainLastResult());
    }

    @Override
    public String describe() {
        return String.format("prefer [%.1f] %s%s", weight, invert ? "not " : "", constraint);
    }

    public String toString() {
        return describe() + ".\n";
    }
}
