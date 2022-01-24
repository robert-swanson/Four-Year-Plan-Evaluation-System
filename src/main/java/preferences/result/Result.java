package preferences.result;

import preferences.explanation.Explainable;
import preferences.value.Value;

public abstract class Result<ValueType extends Value> implements Explainable {
    protected Double calculatedScore;
    protected Boolean calculatedCheck;

    public interface ResultChecker {
        boolean check(Value value);
    }
    public interface ResultScorer {
        double score(Value value);
    }

    public abstract void scoreResult(ResultScorer resultScorer, String scoreFunctionDesc, String checkExplanation);
    public abstract void checkResult(ResultChecker resultChecker, String checkExplanation);

    public Double getLastScore() {
        return calculatedScore;
    }
    public Boolean getCalculatedCheck() {
        return calculatedCheck;
    }

    @Override
    public String toString() {
        return describe();
    }
}
