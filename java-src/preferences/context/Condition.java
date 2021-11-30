package preferences.context;

import preferences.constraints.RequireableConstraint;
import preferences.explanation.specification.ConditionResultExplanation;
import preferences.explanation.Explainable;
import preferences.explanation.Explanation;
import preferences.scoring.Score;

public abstract class Condition implements Explainable {
    public abstract boolean evaluate(Context context);
    public abstract String describe();
    public abstract Explanation explainLastResult();
    private Score lastScore;

    public Score getLastScore() {
        return lastScore;
    }

    public And and(Condition other) {
        return new And(this, other);
    }

    public Or or(Condition other) {
        return new Or(this, other);
    }

    public Not not() {
        return new Not(this);
    }

    public static class ConstraintCondition extends Condition {
        RequireableConstraint requireableConstraint;

        public ConstraintCondition(RequireableConstraint requireableConstraint) {
            this.requireableConstraint = requireableConstraint;
        }

        @Override
        public boolean evaluate(Context context) {
            return requireableConstraint.fulfilled(context);
        }

        @Override
        public String describe() {
            return requireableConstraint.toString();
        }

        @Override
        public Explanation explainLastResult() {
            return requireableConstraint.explainLastResult();
        }

        @Override
        public String toString() {
            return requireableConstraint.toString();
        }
    }

    public static class And extends Condition {
        private final Condition left;
        private final Condition right;

        public And(Condition left, Condition right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(Context context) {
            return left.evaluate(context) && right.evaluate(context);
        }


        @Override
        public Explanation explainLastResult() {
            return new ConditionResultExplanation.AndExplanation(this, left.explainLastResult(), right.explainLastResult());
        }

        @Override
        public String describe() {
            return String.format("(%s and %s)", left, right);
        }

        @Override
        public String toString() {
            return describe();
        }
    }

    public static class Or extends Condition {
        private final Condition left;
        private final Condition right;

        public Or(Condition left, Condition right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean evaluate(Context context) {
            return left.evaluate(context) || right.evaluate(context);
        }

        @Override
        public String describe() {
            return String.format("(%s or %s)", left, right);
        }

        @Override
        public Explanation explainLastResult() {
            return new ConditionResultExplanation.OrExplanation(this, left.explainLastResult(), right.explainLastResult());
        }

        @Override
        public String toString() {
            return describe();
        }
    }

    public static class Not extends Condition {
        private final Condition condition;

        public Not(Condition condition) {
            this.condition = condition;
        }

        @Override
        public boolean evaluate(Context context) {
            return !condition.evaluate(context);
        }


        @Override
        public Explanation explainLastResult() {
            return new ConditionResultExplanation.NotExplanation(this, condition.explainLastResult());
        }

        @Override
        public String describe() {
            return String.format("not %s", condition);
        }

        @Override
        public String toString() {
            return String.format("not %s", condition);
        }
    }

    public static Condition True = new True();

    private static class True extends Condition {
        True(){}

        @Override
        public boolean equals(Object obj) {
            return obj instanceof True;
        }

        @Override
        public boolean evaluate(Context context) {
            return true;
        }

        @Override
        public String describe() {
            return "True";
        }

        @Override
        public Explanation explainLastResult() {
            return null;
        }

        @Override
        public String toString() {
            return "TRUE";
        }
    }
}
