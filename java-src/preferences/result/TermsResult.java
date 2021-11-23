package preferences.result;

import java.util.ArrayList;

public class TermsResult<ValueType extends Value> extends Result<ValueType> {
    private final ArrayList<ValueType> values;

    public TermsResult(String valueDescription) {
        super(valueDescription);
        values = new ArrayList<>();
    }

    @Override
    public void scoreResult(ResultScorer resultScorer, String scoreFunctionDesc, String checkExplanation) {
        double score = 0;
        StringBuilder inputs = new StringBuilder();
        StringBuilder scores = new StringBuilder();
        for (ValueType value : values) {
            double scorePart = resultScorer.score(value);
            score += scorePart;
            inputs.append(value.toString()).append(", ");
            scores.append(String.format("%.2f", scorePart)).append(", ");
        }
        calculatedScore = score / values.size();

        if (!values.isEmpty()) {
            inputs.delete(inputs.length()-2,inputs.length());
            scores.delete(scores.length()-2,scores.length());
        }
        evaluationExplanation = String.format("f([%s]) = [%s], %.2f/%d = %.2f, where f(x) is: %s (to emulate \"%s\")\n", inputs, scores, score, values.size(), calculatedScore, scoreFunctionDesc, checkExplanation);
    }

    @Override
    public void checkResult(ResultChecker resultChecker, String checkExplanation) {
        for (ValueType value : values) {
            if (!resultChecker.check(value)) {
                calculatedCheck = false;
                evaluationExplanation = String.format("Condition Failed at Least Once: %s, for value = %s\n", checkExplanation, value);
                return;
            }
        }
        calculatedCheck = true;
        evaluationExplanation = String.format("Condition Passed for All Values: %s\n", checkExplanation);
    }

    public ArrayList<ValueType> getValues() {
        return values;
    }

    public void addValue(ValueType v) {
        values.add(v);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        values.forEach(value -> {
            if (value == values.get(0)) {
                stringBuilder.append(value.toString());
            } else {
                stringBuilder.append(", ").append(value.toString());
            }
        });
        return String.format("%s = [%s]", resultDescription, stringBuilder);
    }
}
