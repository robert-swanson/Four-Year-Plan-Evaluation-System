package generation;

import exceptions.LinkingException;
import exceptions.PlanException;
import objects.Link;
import objects.catalog.Catalog;
import objects.catalog.Course;
import objects.catalog.Department;
import objects.misc.*;
import objects.offerings.*;
import objects.plan.Plan;
import objects.plan.PlanTerm;

import java.util.*;

public class RandomGenerator {
    private final int numDepartments, numCourses, numTerms, numOfferings;
    private final boolean onlySemesters = true;
    private Random random;
    private Catalog catalog;
    private Offerings offerings;
    private Link link;


    public RandomGenerator(int numDepartments, int numCourses, int numTerms, int numOfferings) {
        this.numDepartments = numDepartments;
        this.numCourses = numCourses;
        this.numTerms = numTerms;
        this.numOfferings = numOfferings;
        this.random = new Random(0);

        try {
            catalog = generateCatalog();
            offerings = generateOfferings();
            link = new Link(catalog, offerings);
        } catch (LinkingException e) {
            e.printStackTrace();
        }
    }

    public Plan generateRandomPlan() {
        LinkedHashMap<TermYear, PlanTerm> termsMap = new LinkedHashMap<>();
        Plan plan = new Plan(-1, "randomly generated plan", termsMap);
        for (int termI = 0; termI < numTerms; termI++) {
            TermYear termYear = generateTermYear(termI);
            ArrayList<SectionID> sectionArray = new ArrayList<>();
            PlanTerm planTerm = new PlanTerm(sectionArray, termYear);
            termsMap.put(termYear, planTerm);

            for (int courseI = 0; courseI < random.nextInt(4, 7); courseI++) {
                sectionArray.add(getRandomCourseOffering(termYear));
            }
        }
        try {
            plan.link(link);
        } catch (PlanException e) {
            e.printStackTrace();
        }
        return plan;
    }

    public CourseID getRandomCourse() {
        String department = generateDepartmentPrefix(random.nextInt(numDepartments));
        return generateCourseID(department, random.nextInt(numCourses));
    }

    public SectionID getRandomCourseOffering() {
        return getRandomCourseOffering(getRandomTermYear());
    }

    public TermYear getRandomTermYear() {
        return generateTermYear(random.nextInt(numTerms));
    }

    public SectionID getRandomCourseOffering(TermYear termYear) {
        try {
            TermOfferings termOfferings = offerings.getOfferings(termYear);
            String department = generateDepartmentPrefix(random.nextInt(numDepartments));
            CourseID courseID = generateCourseID(department, random.nextInt(numCourses));
            CourseOfferings offerings = termOfferings.getCoursesOfferings().get(courseID);
            List<CourseOffering> courseOfferings = offerings.getOfferings();
            CourseOffering courseOffering = courseOfferings.get(random.nextInt(courseOfferings.size()));
            return courseOffering.getCrn();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Offerings generateOfferings() {
        LinkedHashMap<TermYear, TermOfferings> termsOfferingsList = new LinkedHashMap<>();
        Offerings offerings = new Offerings(termsOfferingsList);

        for (int termI = 0; termI < numTerms; termI++) {
            TermYear termYear = generateTermYear(termI);
            LinkedHashMap<CourseID, CourseOfferings> termOfferingsMap = new LinkedHashMap<>();
            TermOfferings termOfferings = new TermOfferings(termOfferingsMap);
            termsOfferingsList.put(termYear, termOfferings);

            for (int departmentI = 0; departmentI < numDepartments; departmentI++) {
                String departmentPrefix = generateDepartmentPrefix(departmentI);

                for (int courseI = 0; courseI < numCourses; courseI++) {
                    CourseID courseID = generateCourseID(departmentPrefix, courseI);
                    LinkedList<CourseOffering> offeringsList = new LinkedList<>();
                    CourseOfferings courseOfferings = new CourseOfferings(offeringsList);
                    termOfferingsMap.put(courseID, courseOfferings);

                    for (int offeringI = 0; offeringI < numOfferings; offeringI++) {
                        SectionID crn = new SectionID(String.format("%d-%d-%d-%d", departmentI, termI, courseI, offeringI));
                        CourseOffering.OfferingType offeringType = (courseI%5 == 4 && offeringI % 2 == 1) ? CourseOffering.OfferingType.lab : CourseOffering.OfferingType.lecture;
                        int credits = (courseI % 10 < 4) ? 3 : ( (courseI % 10 < 7) ? 1 : 4 );
                        String[] allProfessors = {"Professor X", "Professor Y", "Professor Z"};
                        String[] professors = Arrays.copyOfRange(allProfessors, 0, courseI%3);
                        CourseOffering.OfferingLocation location = new CourseOffering.OfferingLocation(departmentPrefix, Integer.toString(courseI));
                        int maxEnrolled = offeringI * courseI * departmentI * 3 % 20 + 10;
                        int numEnrolled = offeringI * courseI * departmentI * 5 % maxEnrolled;
                        int startMonth = switch (termYear.term) { case jterm -> 1; case spring -> 2; case summer -> 5; case fall -> 9; case winter -> 12; };
                        int endMonth = switch (termYear.term) { case jterm -> 1; case spring -> 4; case summer -> 8; case fall -> 11; case winter -> 12; };
                        objects.misc.Date startDate = new objects.misc.Date(termYear.year(), startMonth, 1);
                        objects.misc.Date endDate = new objects.misc.Date(termYear.year(), endMonth, 27);

                        // Meetings
                        ArrayList<Weekday> weekdays = new ArrayList<>();
                        if (offeringI % 2 == 0) { // MWF
                            weekdays.add(Weekday.monday);
                            weekdays.add(Weekday.wednesday);
                            weekdays.add(Weekday.friday);
                        } else if (courseI % 2 == 0) { // T
                            weekdays.add(Weekday.tuesday);
                        } else { // R
                            weekdays.add(Weekday.thursday);
                        }
                        Time startTime = (new Time("8:00 AM")).addMinutes(30*(courseI%16));
                        Time endTime = (new Time("8:50 AM")).addMinutes(30*(courseI%16));
                        Meeting meeting = new Meeting(weekdays, startTime, endTime);
                        LinkedList<Meeting> meetings = new LinkedList<>();
                        meetings.add(meeting);

                        CourseOffering courseOffering = new CourseOffering(crn, offeringI, offeringType, credits, professors, location, numEnrolled, maxEnrolled, startDate, endDate, meetings);
                        offeringsList.add(courseOffering);
                    }
                }
            }
        }

        return offerings;
    }

    private TermYear generateTermYear(int index) {
        CatalogYear firstYear = new CatalogYear("2018-2019");
        TermYear.Term firstTerm = TermYear.Term.fall;

        if (onlySemesters) {
            int yearIncrement = (index) / 2;
            TermYear.Term term = (index % 2 == 0) ? TermYear.Term.fall : TermYear.Term.spring;
            return new TermYear(term, firstYear.increment(yearIncrement));
        } else {
            int yearIncrement = (index + firstTerm.intVal()) / 5;
            int termVal = (index + firstTerm.intVal()) % 5;
            return new TermYear(TermYear.Term.values()[termVal], firstYear.increment(yearIncrement));
        }
    }

    private Catalog generateCatalog() {
        Random random = new Random((long) numDepartments *numCourses);
        assert  numCourses < 1000 : "num courses must be less than 1000";
        assert  numDepartments < 17576 : "num departments must be less than 17576";
        try {
            Catalog catalog = new Catalog("2021-2022");
            ArrayList<Department> departments = new ArrayList<>();
            catalog.departments = departments;

            for (int departmentIndex = 0; departmentIndex < numDepartments; departmentIndex++) {
                String departmentPrefix = generateDepartmentPrefix(departmentIndex);
                Department department = new Department(String.format("department %d (%s)", departmentIndex, departmentPrefix), "this department is a randomly generated department");
                departments.add(department);

                ArrayList<Course> courses = new ArrayList<>();
                department.courses = courses;
                CourseID lastCourse = null;

                for (int courseIndex = 0; courseIndex < numCourses; courseIndex++) {
                    CourseID courseID = generateCourseID(departmentPrefix, courseIndex);
                    Course course = new Course(courseID);
                    courses.add(course);

                    course.prefix = departmentPrefix;
                    course.number =  (100 + courseIndex) % 1000;
                    course.name = String.format("Course %s",course.number);
                    course.courseTags = new ArrayList<>();
                    if (courseIndex % 25 == 0) {
                        course.courseTags.add((char)('A'+(courseIndex%26))+"");
                    }
                    course.description = String.format("Description for %s", courseID);
                    course.prerequisites = new ArrayList<>();
                    if (courseIndex % 3 == 1) {
                        course.prerequisites.add(lastCourse);
                    }
                    course.offeringPattern = Course.OfferingPattern.always;

                    lastCourse = courseID;
                }
            }
            return catalog;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private String generateDepartmentPrefix(int index) {
        return ""+(char)('A'+(index/(26*26)%26)) + (char)('A'+(index/26%26)) + (char)('A'+(index%26));
    }

    private CourseID generateCourseID(String departmentPrefix, int index) {
        return new CourseID(String.format("%s-%03d%s", departmentPrefix, (100 + index) % 1000, ((index % 25 == 0) ? (""+(char)('A'+(index%26))) : "")));
    }
}
