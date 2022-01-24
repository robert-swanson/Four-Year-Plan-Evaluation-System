package preferences.condition;

import preferences.context.Context;
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

    public AndCondition and(Condition other) {
        return new AndCondition(this, other);
    }

    public OrCondition or(Condition other) {
        return new OrCondition(this, other);
    }

    public NotCondition not() {
        return new NotCondition(this);
    }

    public static Condition True = new TrueCondition();
}
