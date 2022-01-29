package preferences.scoring;

import preferences.explanation.Explanation;

public class SigmoidScoringFunction implements ScoreFunction {
    ScoreBound lower, upper;
    double middle;

    public SigmoidScoringFunction(ScoreBound lower, ScoreBound upper) {
        this.lower = lower;
        this.upper = upper;
        this.middle = Math.abs(lower.value - upper.value);
    }

    @Override
    public double score(double value) {
        double normalizedValue = normalize(value, lower.value, upper.value);
        if ((value <= middle ? lower : upper).type == ScoreBound.BoundType.hard) {
            return linear(normalizedValue);
        } else {
            return sigmoid(normalizedValue);
        }
    }

    private double sigmoid(double value) {
        return 1 / (1 + Math.pow(9, -value));
    }

    private double linear(double value) {
        return Math.max(Math.min(value + 0.5, 1), 0);
    }

    private double normalize(double value, double lower, double upper) {
        return (value - lower) / (upper - lower) - 0.5;
    }

    @Override
    public Explanation explainLastResult() {
        return null;
    }

    @Override
    public String describe() {
        if (lower.type == upper.type) {
            return String.format("Proportional with %s bounds at %.2f (lower) and %.2f (upper)", lower.type.toString(), lower.value, upper.value);
        } else {
            return String.format("Proportional with a %s lower bound at %.2f and %s upper bound at %.2f", lower.type.toString(), lower.value, upper.type.toString(), upper.value);
        }
    }

    @Override
    public String toString() {
        return describe();
    }
}
