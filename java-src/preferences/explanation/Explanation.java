package preferences.explanation;

public abstract class Explanation {
    String description;
    public Explanation(Explainable explainable) {
        if (explainable == null) {
            this.description = "not computed";
        } else {
            this.description = explainable.describe();
        }
    }

    public static class NotComputed extends Explanation {
        public NotComputed() {
            super(null);
        }
    }
}
