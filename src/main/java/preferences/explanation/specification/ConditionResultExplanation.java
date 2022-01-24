package preferences.explanation.specification;

import preferences.condition.AndCondition;
import preferences.condition.Condition;
import preferences.condition.NotCondition;
import preferences.condition.OrCondition;
import preferences.explanation.Explanation;

public abstract class ConditionResultExplanation extends Explanation {
    public ConditionResultExplanation(Condition condition) {
        super(condition);
    }

    public static class AndExplanation extends ConditionResultExplanation {
        Explanation left, right;
        public AndExplanation(AndCondition andCondition, Explanation left, Explanation right) {
           super(andCondition);
           this.left = left;
           this.right = right;
        }
    }

    public static class OrExplanation extends ConditionResultExplanation {
        Explanation left, right;
        public OrExplanation(OrCondition orCondition, Explanation left, Explanation right) {
            super(orCondition);
            this.left = left;
            this.right = right;
        }
    }

    public static class NotExplanation extends ConditionResultExplanation {
        Explanation value;
        public NotExplanation(NotCondition notCondition, Explanation value) {
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
