package preferences.result;

import preferences.explanation.Explanation;
import preferences.value.Value;

import java.util.ArrayList;

public class DaysResult<ValueType extends Value> extends Result<ValueType> {
    private final ArrayList<ArrayList<ArrayList<ValueType>>> values;
    private final ArrayList<ArrayList<Double>> weights;

    public DaysResult() {
        values = new ArrayList<>();
        weights = new ArrayList<>();
    }


    @Override
    public void scoreResult(ResultScorer resultScorer, String scoreFunctionDesc, String checkExplanation) {
        double scoreSum = 0.0;
        int weekdayCount = 0;
        StringBuilder inputs = new StringBuilder("[  [ [");
        StringBuilder scores = new StringBuilder("[  [ [");


        for (int termI = 0; termI < values.size(); termI++) {
            for (int weekI = 0; weekI < values.get(termI).size(); weekI++) {
                double weekScoreSum = 0.0;
                for (ValueType value : values.get(termI).get(weekI)) {
                    double scorePart = resultScorer.score(value);
                    weekScoreSum += scorePart;
                    weekdayCount++;
                    inputs.append(value.toString()).append(", ");
                    scores.append(String.format("%.2f",scorePart)).append(", ");
                }
                double weight = weights.get(termI).get(weekI);
                scoreSum += weekScoreSum * weight;
                if (!values.get(termI).get(weekI).isEmpty()) {
                    inputs.delete(inputs.length()-2, inputs.length());
                    scores.delete(scores.length()-2, scores.length());
                }
                inputs.append("], [");
                scores.append(String.format("] --> (%.2f * %.2f = %.2f), [", weekScoreSum, weight, weekScoreSum*weight));
            }

            if (values.get(termI).size() > 0) {
                inputs.delete(inputs.length()-3, inputs.length());
                scores.delete(scores.length()-3, scores.length());
            }
            inputs.append("],  [");
            scores.append("],  [");
        }
        calculatedScore = scoreSum / weekdayCount; // Score = Total / # Weekdays in context

        if (values.size() > 0) {
            inputs.delete(inputs.length()-4, inputs.length());
            scores.delete(scores.length()-4, scores.length());
        }
        inputs.append("]");
        scores.append(String.format("] --> (%.2f / %d = %.2f)", scoreSum, weekdayCount, calculatedScore));

//        evaluationExplanation = String.format("f([%s]) = [%s]\nwhere f(x) is: %s (to emulate \"%s\")\n", inputs, scores, scoreFunctionDesc, checkExplanation);
    }

    @Override
    public void checkResult(ResultChecker resultChecker, String checkExplanation) {
        for (ArrayList<ArrayList<ValueType>> term : values) {
            for (ArrayList<ValueType> week : term) {
                for (ValueType value : week) {
                    if (!resultChecker.check(value)) {
                        calculatedCheck = false;
                        return;
                    }
                }
            }
        }
        calculatedCheck = true;
    }

    public void addTermValue(ArrayList<ArrayList<ValueType>> termValue, ArrayList<Double> weights) {
        this.values.add(termValue);
        this.weights.add(weights);
    }

    @Override
    public Explanation explainLastResult() {
        return null;
    }

    @Override
    public String describe() {
        return String.format("values: %s weights: %s", values, weights);
    }
}
