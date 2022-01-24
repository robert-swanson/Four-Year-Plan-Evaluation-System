package preferences.result;

import preferences.explanation.Explanation;
import preferences.value.Value;

public class PlanResult<ValueType extends Value> extends Result<ValueType> {
    private final Value value;

    public PlanResult(Value value) {
        this.value = value;
    }

    public Value getValue() {
        return value;
    }

    @Override
    public void scoreResult(ResultScorer resultScorer, String scoreFunctionDesc, String checkExplanation) {
        calculatedScore = resultScorer.score(value);
    }

    @Override
    public void checkResult(ResultChecker resultChecker, String checkExplanation) {
        calculatedCheck = resultChecker.check(value);
    }

    @Override
    public Explanation explainLastResult() {
        return null;
    }

    @Override
    public String describe() {
        return value.toString();
    }
}
