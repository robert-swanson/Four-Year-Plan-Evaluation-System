package preferences.scoring;

public class OptimumScoringFunction implements ScoreFunction {
    double middle;
    ScoreBound lowerDeviance, upperDeviance;

    public OptimumScoringFunction(double middle, ScoreBound lowerDeviance, ScoreBound upperDeviance) {
        this.middle = middle;
        this.lowerDeviance = lowerDeviance;
        this.upperDeviance = upperDeviance;
    }

    public OptimumScoringFunction(double middle, ScoreBound deviance) {
        this.middle = middle;
        this.lowerDeviance = deviance;
        this.upperDeviance = deviance;
    }

    @Override
    public String toString() {
        if (lowerDeviance == upperDeviance) {
            return String.format("Optimum Function centered at %.2f with %s deviance of %.2f", middle, lowerDeviance.type, lowerDeviance.value);
        } else {
            return String.format("Optimum Function centered at %.2f with %s lower deviance of %.2f, and %s upper deviance of %.2f", middle, lowerDeviance.type, lowerDeviance.value, upperDeviance.type, upperDeviance.value);
        }
    }

    @Override
    public double score(double value) {
        double deviance = value <= middle ? lowerDeviance.value : upperDeviance.value;
        double normalized = normalize(value, middle, deviance);
        if ((value <= middle ? lowerDeviance : upperDeviance).type == ScoreBound.BoundType.hard) {
            return square(normalized);
        } else {
            return power(normalized);
        }
    }

    private double power(double value) {
        return Math.pow(0.25, value * value);
    }

    private double square(double value) {
        return Math.max(1 - value * value, 0.0);
    }

    private double normalize(double value, double middle, double deviance) {
        return (value - middle) / deviance;
    }
}
