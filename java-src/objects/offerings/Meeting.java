package objects.offerings;

import objects.Link;
import objects.Linkable;
import objects.misc.Time;
import objects.misc.TimeRange;
import objects.misc.Weekday;
import java.util.ArrayList;


public class Meeting {
    private ArrayList<Weekday> days;
    private Time startTime;
    private Time endTime;

    public ArrayList<Weekday> getDays() {
        return days;
    }

    public Time getStartTime() {
        return startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public TimeRange getTimeRange() {
        return new TimeRange(startTime, endTime);
    }

    public int getMeetingMinutes() {
        int meetingMinutes = startTime.minutesUntil(endTime);
        assert meetingMinutes >= 0: "negative meeting minutes";
        return meetingMinutes;
    }

    private transient CourseOffering courseOffering;
    public CourseOffering getCourseOffering() {
        return courseOffering;
    }


    public void setCourseOffering(CourseOffering courseOffering) {
        this.courseOffering = courseOffering;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", courseOffering.toString(), getTimeRange().toString());
    }
}
