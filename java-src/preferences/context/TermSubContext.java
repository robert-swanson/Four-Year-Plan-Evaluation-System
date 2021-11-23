package preferences.context;

import objects.misc.Date;
import objects.misc.SystemConfiguration;
import objects.misc.TermYear;
import objects.misc.Weekday;
import objects.offerings.CourseOffering;
import objects.offerings.Meeting;
import objects.plan.PlanTerm;
import preferences.explanation.Explainable;
import preferences.explanation.context.TermSubContextExplanation;
import preferences.context.iterables.courseoffering.TermCourseOfferingIterable;
import preferences.context.iterables.courseoffering.TermWeekdayCourseOfferingIterable;
import preferences.context.iterables.courseoffering.WeekdayCourseOfferingIterable;
import preferences.context.iterables.meeting.TermMeetingIterable;
import preferences.context.iterables.meeting.TermWeekdayMeetingIterable;

import java.util.*;

/**
 * Contains Meetings and CourseOfferings for a term (sub context: weeks/term segments)
 */

public class TermSubContext implements Explainable {
    // Non Contextual
    PlanTerm planTerm;
    Stack<Set<CourseOffering>> courseOfferingsStack;
    Stack<ArrayList<WeekSubContext>> weekSubContextsStack;
    private TermYear termYear;

    // Initialization

    public TermSubContext(PlanTerm planTerm) {
        // Non-Contextual
        this.planTerm = planTerm;
        courseOfferingsStack = new Stack<>();
        courseOfferingsStack.push(new HashSet<>(planTerm.getCourseOfferings()));
        termYear = planTerm.getTermYear();

        // Contextual
        TreeSet<Date> semesterChangeDates = getSemesterChangeDates(planTerm);
        setWeekSubContexts(semesterChangeDates);
        setWeights();
    }

    TermSubContext(TermSubContext termSubContext, WeekSubContext weekSubContext, WeekdaySubContext weekdaySubContext) {
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
        Date startDate = null;
        ArrayList<WeekSubContext> weekSubContexts = new ArrayList<>();
        for (Date endDate : semesterChangeDates) {
            weekSubContexts.add(createWeekSubContext(planTerm, startDate, endDate));
            startDate = endDate;
        }
        weekSubContexts.add(createWeekSubContext(planTerm, startDate, null)); // Last Segment
        weekSubContextsStack = new Stack<>();
        weekSubContextsStack.push(weekSubContexts);
    }

    private WeekSubContext createWeekSubContext(PlanTerm planTerm, Date startDate, Date endDate) {
        LinkedList<Meeting> meetingsInSemesterSegment = new LinkedList<>();
        for (CourseOffering courseOffering : planTerm.getCourseOfferings()) {
            if (courseInWeekContext(startDate, endDate, courseOffering)) {
                meetingsInSemesterSegment.addAll(courseOffering.getMeetings());
            }
        }
        return new WeekSubContext(startDate, endDate, meetingsInSemesterSegment);
    }

    private boolean courseInWeekContext(Date weekContextStart, Date weekContextEnd, CourseOffering courseOffering) {
        Date courseStart = courseOffering.getStartDate();
        Date courseEnd = courseOffering.getEndDate();
        boolean endsTooEarly = courseEnd != null && weekContextStart != null && Date.datesInOrder(courseEnd, weekContextStart);
        boolean startsTooLate = courseStart != null && weekContextEnd != null && Date.datesInOrder(weekContextEnd, courseStart);
        return !(endsTooEarly || startsTooLate);
    }

    private void setWeights() {
        int usedWeeks = 0, totalWeeks = SystemConfiguration.weeksInTermYear(planTerm.getTermYear());
        for (WeekSubContext weekSubContext : getWeekSubContexts()) {
            if (weekSubContext.startDate != null && weekSubContext.endDate != null) { // Not first or last segment
                int segmentWeeks = weekSubContext.startDate.weeksDifference(weekSubContext.endDate);
                usedWeeks += segmentWeeks;
                weekSubContext.setWeight((double)segmentWeeks/totalWeeks);
            }
        }
        // Finalizes Weights
        if (getWeekSubContexts().size() == 1) { // 1 segment, must add one weight (100%)
            getWeekSubContexts().get(0).setWeight(1.0);
        } else { // >1 segments set first and last weight
            double weight = ((double)totalWeeks-usedWeeks)/totalWeeks/2.0;
            getWeekSubContexts().get(0).setWeight(weight); // First weight estimation
            getWeekSubContexts().get(getWeekSubContexts().size()-1).setWeight(weight); // Last weight estimation
        }
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

    @Override
    public TermSubContextExplanation explainLastResult() {
        return new TermSubContextExplanation(this);
    }

    @Override
    public String describe() {
        return termYear.toString();
    }
}
