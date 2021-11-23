package preferences.explanation;

public abstract class Explanation {
    String description;
    public Explanation(Explainable explainable) {
        this.description = explainable.describe();
    }
}
