package objects.catalog;

import exceptions.CatalogException;
import exceptions.JSONParseException;
import objects.Linkable;
import objects.misc.CatalogYear;
import objects.misc.CourseID;
import objects.Link;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Catalog implements Linkable {
    final CatalogYear catalogYear;
    private ArrayList<Department> departments;

    public Catalog(String catalogYear) throws JSONParseException {
        this.catalogYear = new CatalogYear(catalogYear);
    }

    private transient Link link;
    private transient LinkedHashMap<CourseID, Course> courses;
    @Override
    public void link(Link l) throws CatalogException {
        link = l;
        courses = new LinkedHashMap<>();
        for (Department department: departments) {
            for (Course course: department.getCourses()) {
                this.courses.put(course.courseID, course);
                course.link(l);
            }
        }
    }

    public Course getCourse(CourseID courseID) throws CatalogException {
        if (courses.containsKey(courseID)) {
            return courses.get(courseID);
        } else {
            throw new CatalogException.CourseNotFoundException(String.format("Course with ID '%s' doesn't exist in catalog", courseID));
        }
    }
}
