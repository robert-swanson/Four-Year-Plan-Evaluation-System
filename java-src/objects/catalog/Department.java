package objects.catalog;

import java.util.ArrayList;

public class Department {
    final String name;
    final String description;
    public ArrayList<Course> courses;

    public Department(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Iterable<Course> getCourses() {
        return courses;
    }
}
