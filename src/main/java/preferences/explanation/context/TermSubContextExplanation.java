package preferences.explanation.context;

import preferences.context.TermSubContext;
import preferences.explanation.Explanation;

import java.util.ArrayList;
import java.util.LinkedList;

public class TermSubContextExplanation extends Explanation {
    LinkedList<String> courses;
    ArrayList<WeekSubContextExplanation> termSegments;
    public TermSubContextExplanation(TermSubContext termSubContext) {
        super(termSubContext);
        this.courses = new LinkedList<>();
        this.termSegments = new ArrayList<>();
        termSubContext.getCourseOfferings().forEach(courseOffering -> this.courses.add(courseOffering.getCourse().toString()));
        termSubContext.getWeekSubContexts().forEach(weekSubContext -> this.termSegments.add(weekSubContext.explainLastResult()));
    }
}
