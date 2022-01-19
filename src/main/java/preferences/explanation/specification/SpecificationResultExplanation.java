package preferences.explanation.specification;

import preferences.explanation.Explanation;
import preferences.scoring.Score;
import preferences.specification.Specification;

public abstract class SpecificationResultExplanation extends Explanation {
    String score;

    public SpecificationResultExplanation(Specification specification) {
        super(specification);
        Score score = specification.getLastResult();
        this.score = (score == null ? "not computed" : score.describe());
    }
}
