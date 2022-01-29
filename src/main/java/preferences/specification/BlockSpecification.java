package preferences.specification;

import preferences.context.Context;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;
import psl.PSLGenerator;

import java.util.ArrayList;

public class BlockSpecification extends Specification {
    String name;
    SpecificationList specification;
    ArrayList<PreferenceSpecification.Priority> priorities;

    public BlockSpecification(String name, SpecificationList specification, ArrayList<PreferenceSpecification.Priority> priorities) {
        this.name = name;
        this.specification = specification;
        this.priorities = priorities;
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        return specification.evaluate(context, evaluateAll);
    }

    @Override
    public SpecificationResultExplanation explainLastResult() {
        return specification.explainLastResult();
    }

    @Override
    public String describe() {
        return name;
    }

    @Override
    public Specification getSimplifiedSpecification() {
        return specification.getSimplifiedSpecification();
    }

    private String getPriorityList() {
        if (priorities.isEmpty()) return "";
        StringBuilder string = new StringBuilder("(");
        priorities.forEach(priority -> string.append(priority.toPSL()).append(", "));
        string.delete(string.length()-2, string.length());
        return string.append(") ").toString();
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.addPSL(name + " ");
        if (!priorities.isEmpty()) {
            generator.addPSL(getPriorityList());
        }
        specification.generatePSL(generator);
    }
}
