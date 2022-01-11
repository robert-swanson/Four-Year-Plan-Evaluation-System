package objects.offerings;

import exceptions.CatalogException;
import exceptions.OfferingException;
import objects.Linkable;
import objects.catalog.Course;
import objects.misc.CourseID;
import objects.misc.Date;
import objects.Link;
import objects.misc.SectionID;
import objects.misc.TermYear;

import java.util.LinkedList;

public class CourseOffering implements Linkable {
    public enum OfferingType { lecture, lab }
    public static class OfferingLocation {
        String building;
        String roomNumber;

        public OfferingLocation(String building, String roomNumber) {
            this.building = building;
            this.roomNumber = roomNumber;
        }
    }

    public CourseOffering(SectionID crn, int section, OfferingType type, int credits, String[] professors, OfferingLocation offeringLocation, int numEnrolled, int maxEnrolled, Date startDate, Date endDate, LinkedList<Meeting> meetings) {
        this.crn = crn;
        this.section = section;
        this.type = type;
        this.credits = credits;
        this.professors = professors;
        this.location = offeringLocation;
        this.numEnrolled = numEnrolled;
        this.maxEnrolled = maxEnrolled;
        this.startDate = startDate;
        this.endDate = endDate;
        this.meetings = meetings;
    }

    private SectionID crn;
    private int section;
    private OfferingType type;
    private int credits;
    private String[] professors;
    private OfferingLocation location;
    private int numEnrolled;
    private int maxEnrolled;
    private Date startDate;
    private Date endDate;
    private LinkedList<Meeting> meetings;

    private transient Link link;
    private transient Course course;
    transient CourseID courseID; // set by courseOfferings.link()
    transient TermYear termYear; // set by courseOfferings.link()

    @Override
    public String toString() {
        return String.format("%s (%s)", course.toString(), termYear.toString());
    }

    @Override
    public void link(Link l) throws OfferingException { // called by CourseOfferings
        link = l;
        try {
            course = link.getCatalog().getCourse(courseID);
            if (course == null) {
                System.err.printf("Null course for %s\n", courseID);
            }
        } catch (CatalogException catalogException) {
            throw new OfferingException.CourseOfferingLinkingException(String.format("Course offering with SectionID '%s' for course with CourseID '%s' could not be linked to a course in the catalog\n\t-> %s", crn, courseID, catalogException.getMessage()));
        }
        meetings.forEach(meeting -> meeting.setCourseOffering(this));
    }

    public Course getCourse() {
        return course;
    }

    public SectionID getCrn() {
        return crn;
    }

    public int getSection() {
        return section;
    }

    public OfferingType getType() {
        return type;
    }

    public int getCredits() {
        return credits;
    }

    public String[] getProfessors() {
        return professors;
    }

    public OfferingLocation getLocation() {
        return location;
    }

    public int getNumEnrolled() {
        return numEnrolled;
    }

    public int getMaxEnrolled() {
        return maxEnrolled;
    }

    public Date getStartDate() {
        return startDate.getFirstDayOfWeek();
    }

    public Date getEndDate() {
        return endDate.getFirstDayOfNextWeek();
    }

    public LinkedList<Meeting> getMeetings() {
        return meetings;
    }
}
