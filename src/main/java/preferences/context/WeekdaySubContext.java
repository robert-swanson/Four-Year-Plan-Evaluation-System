package preferences.context;

import objects.misc.Weekday;
import objects.offerings.Meeting;
import preferences.explanation.Explainable;
import preferences.explanation.context.WeekdaySubContextExplanation;
import preferences.context.iterables.courseoffering.WeekdayCourseOfferingIterable;
import preferences.context.iterables.meeting.WeekdayMeetingIterable;

import java.util.LinkedList;

/**
 * Contains meetings and course offerings for a given day of the week
 */
public class WeekdaySubContext implements Explainable {
    // Non-Contextual
    Weekday weekday;
    LinkedList<Meeting> meetings;

    public WeekdaySubContext(Weekday weekday, LinkedList<Meeting> meetings) {
        // Non-Contextual
        this.weekday = weekday;
        this.meetings = meetings;
    }

    // Iterators
    public WeekdayMeetingIterable meetingIterable() {
        return new WeekdayMeetingIterable(this);
    }

    public WeekdayCourseOfferingIterable courseOfferingIterable() {
        return new WeekdayCourseOfferingIterable(this);
    }

    // Getters
    public Weekday getWeekday() {
        return weekday;
    }

    public LinkedList<Meeting> getMeetings() {
        return meetings;
    }

    @Override
    public WeekdaySubContextExplanation explainLastResult() {
        return new WeekdaySubContextExplanation(this);
    }

    @Override
    public String describe() {
        return weekday.toString();
    }
}
