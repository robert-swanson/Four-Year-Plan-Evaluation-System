package objects.catalog;

import java.util.ArrayList;

class Department {
    final String name;
    final String description;
    private ArrayList<Course> courses;

    Department(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Iterable<Course> getCourses() {
        return courses;
    }
}
