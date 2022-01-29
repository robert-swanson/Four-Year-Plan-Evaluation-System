package preferences.context;

import objects.misc.Date;
import objects.misc.TermYear;
import objects.misc.Weekday;
import objects.offerings.CourseOffering;
import objects.offerings.Meeting;
import objects.plan.Plan;
import objects.plan.PlanTerm;
import preferences.condition.Condition;
import preferences.context.iterables.courseoffering.TermCourseOfferingIterable;
import preferences.context.iterables.courseoffering.TermWeekdayCourseOfferingIterable;
import preferences.context.iterables.courseoffering.WeekdayCourseOfferingIterable;
import preferences.context.iterables.meeting.TermMeetingIterable;
import preferences.context.iterables.meeting.TermWeekdayMeetingIterable;
import preferences.explanation.Explainable;
import preferences.explanation.context.TermSubContextExplanation;

import java.util.*;

/**
 * Contains Meetings and CourseOfferings for a term (sub context: weeks/term segments)
 */

public class TermSubContext implements Explainable {
    // Non Contextual
    transient Plan plan;
    PlanTerm planTerm;
    Stack<Set<CourseOffering>> courseOfferingsStack;
    Stack<ArrayList<WeekSubContext>> weekSubContextsStack;
    private TermYear termYear;
    Date termStartDate, termEndDate;

    // Initialization

    public TermSubContext(PlanTerm planTerm, Plan plan) {
        // Non-Contextual
        this.plan = plan;
        this.planTerm = planTerm;
        courseOfferingsStack = new Stack<>();
        courseOfferingsStack.push(new HashSet<>(planTerm.getCourseOfferings()));
        termYear = planTerm.getTermYear();

        // Contextual
        TreeSet<Date> semesterChangeDates = getSemesterChangeDates(planTerm);
        termStartDate = semesterChangeDates.first();
        termEndDate = semesterChangeDates.last();
        setWeekSubContexts(semesterChangeDates);
    }

    TermSubContext(TermSubContext termSubContext, WeekSubContext weekSubContext, WeekdaySubContext weekdaySubContext, Plan plan) {
        this.plan = plan;
        planTerm = null;
        courseOfferingsStack = null; // TODO may need to set course offerings, but I'm pretty sure not, so will leave it out

        this.termYear = termSubContext.getTermYear();
        ArrayList<WeekSubContext> weekSubContextsList = new ArrayList<>();
        weekSubContextsList.add(new WeekSubContext(weekSubContext, weekdaySubContext));
        weekSubContextsStack = new Stack<>();
        weekSubContextsStack.push(weekSubContextsList);

        termYear = termSubContext.getTermYear();
    }

    private TreeSet<Date> getSemesterChangeDates(PlanTerm planTerm) {
        TreeSet<Date> semesterChangeDates = new TreeSet<>();
        for (CourseOffering courseOffering : planTerm.getCourseOfferings()) {
            Date start = courseOffering.getStartDate();
            Date end = courseOffering.getEndDate();
            if (start != null) {
                semesterChangeDates.add(start);
            }
            if (end != null) {
                semesterChangeDates.add(end);
            }
        }
        return semesterChangeDates;
    }

    private void setWeekSubContexts(TreeSet<Date> semesterChangeDates) {
        ArrayList<WeekSubContext> weekSubContexts = new ArrayList<>();
        Date startDate = semesterChangeDates.first();
        int totalWeeks = termStartDate.weeksDifference(termEndDate);
        semesterChangeDates.remove(startDate);
        for (Date endDate : semesterChangeDates) {
            double weight = startDate.weeksDifference(endDate) / (double)totalWeeks;
            weekSubContexts.add(createWeekSubContext(planTerm, startDate, endDate, weight));
            startDate = endDate;
        }
        weekSubContextsStack = new Stack<>();
        weekSubContextsStack.push(weekSubContexts);
    }

    private WeekSubContext createWeekSubContext(PlanTerm planTerm, Date startDate, Date endDate, double weight) {
        LinkedList<Meeting> meetingsInSemesterSegment = new LinkedList<>();
        for (CourseOffering courseOffering : planTerm.getCourseOfferings()) {
            if (courseInWeekContext(startDate, endDate, courseOffering)) {
                meetingsInSemesterSegment.addAll(courseOffering.getMeetings());
            }
        }
        return new WeekSubContext(startDate, endDate, meetingsInSemesterSegment, weight, plan);
    }

    private boolean courseInWeekContext(Date weekContextStart, Date weekContextEnd, CourseOffering courseOffering) {
        Date courseStart = courseOffering.getStartDate();
        Date courseEnd = courseOffering.getEndDate();
        boolean endsTooEarly = courseEnd != null && weekContextStart != null && Date.datesInOrder(courseEnd, weekContextStart);
        boolean startsTooLate = courseStart != null && weekContextEnd != null && Date.datesInOrder(weekContextEnd, courseStart);
        return !(endsTooEarly || startsTooLate);
    }

    // Context Filtering
    private Set<CourseOffering> getCourseOfferings(ArrayList<WeekSubContext> newContext) {
        Set<CourseOffering> courseOfferings = new HashSet<>();
        newContext.forEach(weekSubContext ->
                weekSubContext.getWeekdaySubcontexts().forEach((weekday, weekdaySubContext) -> {
                    for (CourseOffering courseOffering : new WeekdayCourseOfferingIterable(weekdaySubContext)) {
                        courseOfferings.add(courseOffering);
                    }
                }));
        return courseOfferings;
    }

    public boolean applyContextFilter(Condition condition) {
        ArrayList<WeekSubContext> newContext = new ArrayList<>();
        for (WeekSubContext weekSubContext : getWeekSubContexts()) {
            if (weekSubContext.applyContextFilter(condition, this)) {
                newContext.add(weekSubContext);
            }
        }
        if (newContext.isEmpty()) {
            return false;
        } else {
            weekSubContextsStack.push(newContext);
            courseOfferingsStack.push(getCourseOfferings(newContext));
            return true;
        }
    }

    public boolean filterDays(Set<Weekday> weekdaySet) {
        ArrayList<WeekSubContext> newContext = new ArrayList<>();
        for (WeekSubContext weekSubContext : getWeekSubContexts()) {
            if (weekSubContext.filterDays(weekdaySet)) {
                newContext.add(weekSubContext);
            }
        }
        if (newContext.isEmpty()) {
            return false;
        } else {
            weekSubContextsStack.push(newContext);
            courseOfferingsStack.push(getCourseOfferings(newContext));
            return true;
        }
    }

    public void duplicateStackTop() {
        weekSubContextsStack.push(getWeekSubContexts());
        getWeekSubContexts().forEach(WeekSubContext::duplicateStackTop);
        courseOfferingsStack.push(getCourseOfferings());
    }

    public void popContext() {
        courseOfferingsStack.pop();
        weekSubContextsStack.pop().forEach(WeekSubContext::popContext);
    }

    // Iterables
    public TermWeekdayMeetingIterable termWeekdayMeetingIterable() {
        return new TermWeekdayMeetingIterable(this);
    }

    public TermWeekdayCourseOfferingIterable termWeekdayCourseOfferingIterable() {
        return new TermWeekdayCourseOfferingIterable(this);
    }

    public TermMeetingIterable termMeetingIterable() {
        return new TermMeetingIterable(this);
    }

    public TermCourseOfferingIterable termCourseOfferingIterable() {
        return new TermCourseOfferingIterable(this);
    }

    // Getters
    public PlanTerm getPlanTerm() {
        return planTerm;
    }

    public Set<CourseOffering> getCourseOfferings() {
        return courseOfferingsStack.peek();
    }

    public ArrayList<WeekSubContext> getWeekSubContexts() {
        return weekSubContextsStack.peek();
    }

    public TermYear getTermYear() {
        return termYear;
    }

    public void assertPlanContext() {
        assert weekSubContextsStack.size() == 1 : "TermSubContext's WeekSubContextsStack ≠ 1";
        assert courseOfferingsStack.size() == 1 : "TermSubContext's CourseOfferingsStack ≠ 1";
        getWeekSubContexts().forEach(WeekSubContext::assertPlanContext);
    }

    @Override
    public TermSubContextExplanation explainLastResult() {
        return new TermSubContextExplanation(this);
    }

    @Override
    public String describe() {
        return termYear.toString();
    }
}
