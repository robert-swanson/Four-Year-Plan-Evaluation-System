package preferences.explanation.context;

import preferences.context.WeekSubContext;
import preferences.explanation.Explanation;

import java.util.LinkedHashMap;

public class WeekSubContextExplanation extends Explanation {
    double weight;
    LinkedHashMap<String, WeekdaySubContextExplanation> weekdays;

    public WeekSubContextExplanation(WeekSubContext weekSubContext) {
        super(weekSubContext);
        this.weight = weekSubContext.getWeight();
        weekdays = new LinkedHashMap<>();
        weekSubContext.getWeekdaySubcontexts().forEach((weekday, weekdaySubContext) -> this.weekdays.put(weekday.toString(), weekdaySubContext.explainLastResult()));
    }
}
