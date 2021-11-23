package objects.catalog;

import exceptions.CatalogException;
import objects.Linkable;
import objects.misc.CourseID;
import objects.Link;
import objects.misc.TermYear;
import objects.offerings.CourseOfferings;

import java.util.ArrayList;

public class Course implements Linkable {
    public Course(CourseID courseID) {
        this.courseID = courseID;
    }

    enum OfferingPattern {
        always, fall, spring, jTerm, summer, oddFall, evenFall, oddSpring, evenSpring
    }

    final CourseID courseID;
    private String name;
    private String prefix;
    private int number;
    private String description;
    private ArrayList<CourseID> prerequisites;
    private OfferingPattern offeringPattern;
    private ArrayList<String> courseTags;

    @Override
    public String toString() {
        return String.format("%s: %s", courseID, name);
    }

    private transient Link link;
    @Override
    public void link(Link l) {
        link = l;
    }

    public CourseOfferings courseOfferingsForTerm(TermYear termYear) throws CatalogException {
        try {
            return link.getOfferings().getOfferings(termYear).getCourseOfferings(courseID);
        } catch (Exception e) {
            throw new CatalogException(String.format("getting offerings for term '%s':\n\t-> %s", termYear.toString(), e));
        }
    }

    public CourseID getCourseID() {
        return courseID;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<CourseID> getPrerequisites() {
        return prerequisites;
    }

    public OfferingPattern getOfferingPattern() {
        return offeringPattern;
    }

    public ArrayList<String> getCourseTags() {
        return courseTags;
    }
}
