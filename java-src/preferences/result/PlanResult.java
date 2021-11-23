package preferences.result;

public class PlanResult<ValueType extends Value> extends Result<ValueType> {
    private final ValueType value;

    public PlanResult(String valueDescription, String explanation, ValueType value) {
        super(valueDescription);
        this.value = value;
        super.setResultExplanation(explanation);
    }

    public ValueType getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%s = [%s]", resultDescription, value.toString());
    }

    @Override
    public void scoreResult(ResultScorer resultScorer, String scoreFunctionDesc, String checkExplanation) {
        calculatedScore = resultScorer.score(value);
        evaluationExplanation = String.format("f(%.2f) = %.2f, where f(x) is: %s (to emulate \"%s\")\n", ((ScalableValue)value).getScalableValue(), calculatedScore, scoreFunctionDesc, checkExplanation);
    }

    @Override
    public void checkResult(ResultChecker resultChecker, String checkExplanation) {
        calculatedCheck = resultChecker.check(value);
        if (calculatedCheck) {
            evaluationExplanation = String.format("Condition Passed: %s (actual value: %s)\n", checkExplanation, value.toString());
        } else {
            evaluationExplanation = String.format("Condition Failed: %s (actual value: %s)\n", checkExplanation, value.toString());
        }
    }
}
