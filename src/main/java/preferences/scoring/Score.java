package preferences.scoring;

public class Score {
    private double scoreAccumulator;
    private double scoreMax;
    private boolean isValid;
    private int requirementsMet = 0;
    private int requirementsViolated = 0;

    public static Score valid() {
        return new Score(true);
    }

    public static Score invalid() {
        return new Score(false);
    }

    private Score(boolean valid) {
        this(0,0,false);
        this.isValid = valid;
        if (valid) requirementsMet++;
        else requirementsViolated++;
    }

    public Score(double score, double weight, boolean invert) {
        this.scoreAccumulator = (invert ? weight * (1 - score) : score*weight);
        this.scoreMax = weight;
        isValid = true;
    }

    public void addScore(Score score) {
        scoreAccumulator += score.scoreAccumulator;
        scoreMax += score.scoreMax;
        isValid &= score.isValid;
        requirementsMet += score.requirementsMet;
        requirementsViolated += score.requirementsViolated;
    }

    public double getScore() {
        return scoreAccumulator / scoreMax;
    }

    public boolean isValid() { return  isValid; }

    public String describe() {
        String reqStr = isValid ? "valid" : "invalid";
        if (!isValid) {
            int totalReqs = requirementsMet+requirementsViolated;
            reqStr += String.format(", %d requirements out of %d unmet", requirementsViolated, totalReqs);
        }
        if (scoreMax != 0) {
            reqStr += String.format(", with score of %.0f%%", getScore()*100);
        }
        return reqStr;
    }

    @Override
    public String toString() {
        return describe();
    }
}
