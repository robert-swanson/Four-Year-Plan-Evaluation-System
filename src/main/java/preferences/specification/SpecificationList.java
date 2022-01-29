package preferences.specification;

import preferences.context.Context;
import preferences.explanation.specification.SpecificationListResultExplanation;
import preferences.explanation.specification.SpecificationResultExplanation;
import preferences.scoring.Score;
import psl.PSLGenerator;

import java.util.LinkedList;

public class SpecificationList extends Specification {
    LinkedList<Specification> specifications;

    public SpecificationList() {
        specifications = new LinkedList<>();
    }

    public SpecificationList(LinkedList<Specification> specifications) {
        this.specifications = specifications;
    }

    public SpecificationList(Specification... specifications) {
        this.specifications = new LinkedList<>();
        for (Specification specification : specifications) {
            addSpecification(specification);
        }
    }

    public void addSpecification(Specification specification) {
        if (specification != null) {
            specifications.add(specification);
        }
    }

    @Override
    public Score evaluate(Context context, boolean evaluateAll) {
        lastScore = Score.valid();
        for (Specification specification : specifications) {
            lastScore.addScore(specification.evaluate(context, evaluateAll));
            if (!lastScore.isValid() && !evaluateAll) {
                return lastScore;
            }
        }
        return lastScore;
    }

    @Override
    public SpecificationResultExplanation explainLastResult() {
        return new SpecificationListResultExplanation(this, specifications);
    }


    @Override
    public Specification getSimplifiedSpecification() {
        return switch (specifications.size()) {
            case 0 -> null;
            case 1 -> specifications.getFirst().getSimplifiedSpecification();
            default -> collapseNestedLists();
        };
    }

    private SpecificationList collapseNestedLists() {
        LinkedList<Specification> newSpecifications = new LinkedList<>();
        for (Specification specification : specifications) {
            Specification simplified = specification.getSimplifiedSpecification();
            if (simplified instanceof SpecificationList) {
                newSpecifications.addAll(((SpecificationList) simplified).specifications);
            } else {
                newSpecifications.add(simplified);
            }
        }
        return new SpecificationList(newSpecifications);
    }

    @Override
    public String describe() {
        return "Specification List";
    }

    @Override
    public void generatePSL(PSLGenerator generator) {
        generator.indent();
        generator.addPSL("{\n");
        specifications.forEach(specification -> specification.generatePSL(generator));
        generator.dedent();
        generator.addPSL("}\n");
    }
}
