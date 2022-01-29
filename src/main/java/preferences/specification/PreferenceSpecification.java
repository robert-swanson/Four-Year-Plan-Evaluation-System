package preferences.specification;

import preferences.constraints.Constraint;
import preferences.context.Context;
import preferences.explanation.specification.PreferenceSpecificationResultExplanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;
import psl.PSLComponent;
import psl.PSLGenerator;

public class PreferenceSpecification extends Specification {
    private final Constraint constraint;
    private final boolean invert;
    private final Priority priority;

    public record Priority(double weight, String name) implements PSLComponent {
        @Override
        public String toString() {
            return name;
        }

        @Override
        public void generatePSL(PSLGenerator generator) {
            generator.addPSL(String.format("%s = %s", name, generator.numToString(weight)));
        }
    }

    public PreferenceSpecification(Constraint constraint, Priority priority, boolean invert){
        this.constraint = constraint;
        this.invert = invert;
        this.priority = priority;
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        lastScore = new Score(constraint.score(context), priority.weight, invert);
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
        return toPSL().replaceAll("\n$","");
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(String.format("prefer %s %s%s.\n", priority, invert ? "not " : "", constraint.toPSL()));
    }
}
