package preferences.result;

public abstract class Result<ValueType extends Value> {
    protected final String resultDescription;
    protected String resultExplanation = "";
    protected String evaluationExplanation = "";
    protected Double calculatedScore;
    protected Boolean calculatedCheck;

    public interface ResultChecker {
        boolean check(Value value);
    }
    public interface ResultScorer {
        double score(Value value);
    }

    public Result(String resultDescription) {
        this.resultDescription = resultDescription;
    }
    public void setEvaluationExplanation(String evaluationExplanation) { this.evaluationExplanation = evaluationExplanation; }

    public String getResultExplanation() {
        return String.format("%s:\n%s%s\n%s", resultDescription, resultExplanation, this, evaluationExplanation);
    }
    public void setResultExplanation(String explanation) {
        this.resultExplanation = explanation;
    }

    public abstract void scoreResult(ResultScorer resultScorer, String scoreFunctionDesc, String checkExplanation);
    public abstract void checkResult(ResultChecker resultChecker, String checkExplanation);

    public String getResultDescription() {
        return resultDescription;
    }

    public Double score() {
        return calculatedScore;
    }

    public Boolean getCalculatedCheck() {
        return calculatedCheck;
    }
}
