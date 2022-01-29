package preferences.scoring;

import preferences.explanation.Explainable;

public interface ScoreFunction extends Explainable {
    double score(double value);
}
