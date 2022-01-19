package preferences.scoring;

public class ScoreBound {
    public enum BoundType { hard, soft }

    public final BoundType type;
    public final double value;

    public ScoreBound(BoundType type, double value) {
        this.type = type;
        this.value = value;
    }
}
