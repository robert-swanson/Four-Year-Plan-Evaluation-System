package preferences.condition;

import preferences.context.Context;
import preferences.explanation.Explainable;
import preferences.explanation.Explanation;
import preferences.scoring.Score;
import psl.PSLComponent;

public abstract class Condition implements Explainable, PSLComponent {
    public abstract boolean evaluate(Context context);
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

    @Override
    public String toString() {
        return toPSL();
    }

    @Override
    public String describe() {
        return toPSL();
    }
}
