package preferences.explanation.context;

import preferences.context.WeekdaySubContext;
import preferences.explanation.Explanation;

import java.util.ArrayList;

public class WeekdaySubContextExplanation extends Explanation {
    ArrayList<String> meetings;
    public WeekdaySubContextExplanation(WeekdaySubContext weekdaySubContext) {
        super(weekdaySubContext);
        meetings = new ArrayList<>();
        weekdaySubContext.getMeetings().forEach(meeting -> this.meetings.add(meeting.toString()));
    }
}
