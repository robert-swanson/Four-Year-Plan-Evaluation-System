package preferences.scoring;

public class Score {
    private double accum;
    private double max;
    private boolean isValid;

    public static Score valid() {
        return new Score(true);
    }

    public static Score invalid() {
        return new Score(false);
    }

    private Score(boolean valid) {
        this(0,0,false);
        this.isValid = valid;
    }

    public Score(double score, double weight, boolean invert) {
        this.accum = (invert ? weight * (1 - score) : score*weight);
        this.max = weight;
        isValid = true;
    }

    public void addScore(Score score) {
        accum += score.accum;
        max += score.max;
        isValid &= score.isValid;
    }

    public double getScore() {
        return accum/max;
    }

    public boolean isValid() { return  isValid; }

    public String describe() {
        if (max == 0) {
            return isValid ? "valid" : "invalid";
        } else {
            return String.format("%s (%.0f%%)", (isValid ? "valid" : "invalid"), getScore()*100);
        }
    }

    @Override
    public String toString() {
        return describe();
    }
}
