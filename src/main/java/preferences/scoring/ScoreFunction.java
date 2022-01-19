package preferences.scoring;

public interface ScoreFunction {
    double score(double value);

    class Boolean implements ScoreFunction {
        @Override
        public double score(double value) {
            return (value == 0 ? 0 : 1);
        }
    }

    class Proportional implements ScoreFunction {
        ScoreBound lower, upper;
        double middle;
        public Proportional(ScoreBound lower, ScoreBound upper) {
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

        @Override
        public String toString() {
            if (lower.type == upper.type) {
                return String.format("Proportional with %s bounds at %.2f (lower) and %.2f (upper)", lower.type.toString(), lower.value, upper.value);
            } else {
                return String.format("Proportional with a %s lower bound at %.2f and %s upper bound at %.2f", lower.type.toString(), lower.value, upper.type.toString(), upper.value);
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
    }

    class Optimum implements ScoreFunction {
        double middle;
        ScoreBound lowerDeviance, upperDeviance;

        public Optimum(double middle, ScoreBound lowerDeviance, ScoreBound upperDeviance) {
            this.middle = middle;
            this.lowerDeviance = lowerDeviance;
            this.upperDeviance = upperDeviance;
        }

        public Optimum(double middle, ScoreBound deviance) {
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
            return Math.pow(0.25, value*value);
        }

        private double square(double value) {
            return Math.max(1 - value * value, 0.0);
        }

        private double normalize(double value, double middle, double deviance) {
            return (value - middle) / deviance;
        }
    }
}
