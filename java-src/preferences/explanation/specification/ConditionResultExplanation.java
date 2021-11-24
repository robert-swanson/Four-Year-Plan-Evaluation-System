package preferences.explanation.specification;

import preferences.context.Condition;
import preferences.explanation.Explanation;

public abstract class ConditionResultExplanation extends Explanation {
    public ConditionResultExplanation(Condition condition) {
        super(condition);
    }

    public static class AndExplanation extends ConditionResultExplanation {
        Explanation left, right;
        public AndExplanation(Condition.And andCondition, Explanation left, Explanation right) {
           super(andCondition);
           this.left = left;
           this.right = right;
        }
    }

    public static class OrExplanation extends ConditionResultExplanation {
        Explanation left, right;
        public OrExplanation(Condition.Or orCondition, Explanation left, Explanation right) {
            super(orCondition);
            this.left = left;
            this.right = right;
        }
    }

    public static class NotExplanation extends ConditionResultExplanation {
        Explanation value;
        public NotExplanation(Condition.Not notCondition, Explanation value) {
            super(notCondition);
            this.value = value;
        }
    }

    public static class TrueExplanation extends ConditionResultExplanation {
        public TrueExplanation() {
            super(Condition.True);
        }
    }
}
