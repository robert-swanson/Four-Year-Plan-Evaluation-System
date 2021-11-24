package psl.listener;

import exceptions.JSONParseException;
import objects.misc.*;
import org.antlr.v4.runtime.tree.TerminalNode;
import preferences.constraints.*;
import preferences.evaluators.TermYearContextEvaluator;
import preferences.context.Condition;
import preferences.context.ContextLevel;
import preferences.evaluators.BooleanContextEvaluator;
import preferences.evaluators.ContextEvaluator;
import preferences.evaluators.ScalableContextEvaluator;
import preferences.evaluators.general.*;
import preferences.evaluators.single.*;
import preferences.evaluators.weekday.MeetingInTimeRangeEvaluator;
import preferences.evaluators.weekday.NumTimeBlocksEvaluator;
import preferences.evaluators.weekday.WeekdayEndTime;
import preferences.evaluators.weekday.WeekdayStartTime;
import preferences.result.ScalableValue;
import preferences.specification.*;
import psl.PSLGrammarBaseListener;
import psl.PSLGrammarParser;

import java.util.*;

public class PSLListener extends PSLGrammarBaseListener {
    private final Stack<PSLParsingContext> parsingContextStack;
    private final HashMap<String, Double> priorities;
    private final HashMap<String, Specification> blocks;

    public PSLListener() {
        priorities = new HashMap<>();
        parsingContextStack = new Stack<>();
        parsingContextStack.push(new PSLParsingContext(ContextLevel.fullPlan));
        blocks = new HashMap<>();
    }

    // Parsing Context Stack
    private void pushContext() {
        parsingContextStack.push(new PSLParsingContext(parsingContextStack.peek().contextLevel));
    }
    private void pushContext(ContextLevel contextLevel) {
        parsingContextStack.push(new PSLParsingContext(contextLevel));
    }

    private PSLParsingContext popContext() {
        return parsingContextStack.pop();
    }

    private ContextLevel getContextLevel() {
        return parsingContextStack.peek().contextLevel;
    }

    public HashMap<String, Specification> getBlocks() {
        assert parsingContextStack.size() == 1;
        return blocks;
    }

    private void addSpecification(Specification specification) {
        parsingContextStack.peek().specifications.push(specification);
    }

    private void addCondition(Condition condition) {
        parsingContextStack.peek().conditions.push(condition);
    }
    private Condition getCondition() {
        return parsingContextStack.peek().conditions.pop();
    }

    private void addConstraint(Constraint constraint) {
        parsingContextStack.peek().constraints.push(constraint);
    }
    private Constraint getConstraint() {
        return parsingContextStack.peek().constraints.pop();
    }

    private void addEvaluator(ContextEvaluator evaluator) {
        parsingContextStack.peek().evaluators.push(evaluator);
    }

    private ContextEvaluator getContextEvaluator() {
        return parsingContextStack.peek().evaluators.pop();
    }

    private ScalableContextEvaluator getScalableEvaluator() {
        ContextEvaluator evaluator = getContextEvaluator();
        assert evaluator instanceof ScalableContextEvaluator : "Evaluator must be ScalableContextEvaluator";
        return (ScalableContextEvaluator) evaluator;
    }

    private BooleanContextEvaluator getBooleanEvaluator() {
        ContextEvaluator evaluator = getContextEvaluator();
        assert evaluator instanceof BooleanContextEvaluator : "Evaluator must be BooleanContextEvaluator";
        return (BooleanContextEvaluator) evaluator;
    }

    @Override
    public void exitPriority(PSLGrammarParser.PriorityContext ctx) {
        double value = (ctx.INT() == null ? Double.parseDouble(ctx.FLOAT().toString()) : Integer.parseInt(ctx.INT().toString()));
        String priorityName = ctx.NAME().toString();
        assert !priorities.containsKey(priorityName);
        priorities.put(priorityName, value);
    }

    @Override
    public void exitBlock(PSLGrammarParser.BlockContext ctx) {
        String blockName = ctx.NAME().getText();
        assert !blocks.containsKey(blockName) : String.format("Duplicate block name '%s'", blockName);
        blocks.put(blockName, popContext().getSpecificationList());
        pushContext(ContextLevel.fullPlan);
    }

    // --- Specifications ---

    // Requirements
    @Override
    public void exitRequirementSpecification(PSLGrammarParser.RequirementSpecificationContext ctx) {
        boolean invert = ctx.NOT() != null;
        RequirementSpecification requirement = new RequirementSpecification((RequireableConstraint) getConstraint(), invert);
        addSpecification(requirement);
    }

    // Preferences
    @Override
    public void exitPreferenceSpecification(PSLGrammarParser.PreferenceSpecificationContext ctx) {
        double weight = 1.0;
        if (ctx.NAME() != null) {
            assert priorities.containsKey(ctx.NAME().toString()) : "Unknown Priority";
            weight = priorities.get(ctx.NAME().toString());
        }
        boolean invert = ctx.NOT() != null;
        PreferenceSpecification preference = new PreferenceSpecification(getConstraint(), weight, invert);
        addSpecification(preference);
    }

    // Specification List
    @Override
    public void enterSpecificationList(PSLGrammarParser.SpecificationListContext ctx) {
        pushContext();
    }

    @Override
    public void exitSpecificationList(PSLGrammarParser.SpecificationListContext ctx) {
        SpecificationList specificationList = popContext().getSpecificationList();
        addSpecification(specificationList);
    }

    // Conditional Specification
    @Override
    public void enterConditionalSpecification(PSLGrammarParser.ConditionalSpecificationContext ctx) {
        pushContext();
    }

    @Override
    public void exitConditionalSpecification(PSLGrammarParser.ConditionalSpecificationContext ctx) {
        ConditionalSpecification conditionalSpecification = popContext().getConditional();
        addSpecification(conditionalSpecification);
    }

    // Contextual Specification


//    @Override
//    public void enterContextualSpecification(PSLGrammarParser.ContextualSpecificationContext ctx) {
//        pushContext();
//    }

    @Override
    public void exitContextLevel(PSLGrammarParser.ContextLevelContext ctx) {
        if (ctx.TERMS() != null) {
            assert getContextLevel() != ContextLevel.days : "Cannot move up to a broader context level";
            pushContext(ContextLevel.terms);
        } else {
            pushContext(ContextLevel.days);
        }
    }

    @Override
    public void exitTermYearList(PSLGrammarParser.TermYearListContext ctx) {
        assert getContextLevel() != ContextLevel.days : "Cannot move up to a broader context level";
        pushContext(ContextLevel.terms);
        ctx.termYear().forEach(termYearContext -> {
            try {
                parsingContextStack.peek().termYears.add(new TermYear(termYearContext.getText()));
            } catch (JSONParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void exitWeekdayList(PSLGrammarParser.WeekdayListContext ctx) {
        pushContext(ContextLevel.days);
        ctx.weekday().forEach(weekdayContext -> {
            try {
                parsingContextStack.peek().weekdays.add(Weekday.fromString(weekdayContext.getText()));
            } catch (JSONParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void exitContextualSpecification(PSLGrammarParser.ContextualSpecificationContext ctx) {
        ContextualSpecification contextualSpecification = popContext().getContextual();
        addSpecification(contextualSpecification);

    }

    // --- Condition ---

    @Override
    public void exitCondition(PSLGrammarParser.ConditionContext ctx) {
        if (ctx.requirableConstraint() != null) {
            addCondition(new Condition.ConstraintCondition((RequireableConstraint) getConstraint()));
        } else if (ctx.AND() != null) {
            Condition right = getCondition(), left = getCondition();
            addCondition(left.and(right));
        } else if (ctx.OR() != null) {
            Condition right = getCondition(), left = getCondition();
            addCondition(left.or(right));
        } else { // NOT
            addCondition(getCondition().not());
        }
    }

    // --- Constraint ---
    // Requireable Constraint

    private ScalableValue getValue(TerminalNode intVal, PSLGrammarParser.TimeContext time, PSLGrammarParser.TermYearContext termYearContext) {
        ScalableValue value = null;
        if (intVal != null) {
            value = new ScalableValue.Numeric(Double.parseDouble(intVal.getText()));
        } else if (time != null) {
            value = new ScalableValue.TimeValue(new Time(time.getText()));
        } else if (termYearContext != null) {
            try {
                value = new ScalableValue.TermYearValue(new TermYear(termYearContext.getText()));
            } catch (JSONParseException e) {
                assert false : "couldn't parse term year: " + termYearContext.getText();
            }
        } else {
            assert false : "no listener support for evaluator type";
        }
        return value;
    }

    @Override
    public void exitMoreConstraint(PSLGrammarParser.MoreConstraintContext ctx) {
        addConstraint(new MoreConstraint(getScalableEvaluator(), getContextLevel()));
    }

    @Override
    public void exitLessConstraint(PSLGrammarParser.LessConstraintContext ctx) {
        addConstraint(new LessConstraint(getScalableEvaluator(), getContextLevel()));
    }

    @Override
    public void exitEqualConstraint(PSLGrammarParser.EqualConstraintContext ctx) {
        ScalableValue value = getValue(ctx.INT(), ctx.time(), ctx.termYear());
        Constraint constraint = new EqualConstraint(getScalableEvaluator(), value, getContextLevel());
        addConstraint(constraint);
    }

    @Override
    public void exitGreaterThanConstraint(PSLGrammarParser.GreaterThanConstraintContext ctx) {
        ScalableValue value = getValue(ctx.INT(), ctx.time(), ctx.termYear());
        Constraint constraint = new GreaterThanConstraint(getScalableEvaluator(), value, getContextLevel());
        addConstraint(constraint);
    }

    @Override
    public void exitGreaterThanOrEqualConstraint(PSLGrammarParser.GreaterThanOrEqualConstraintContext ctx) {
        ScalableValue value = getValue(ctx.INT(), ctx.time(), ctx.termYear());
        Constraint constraint = new GreaterThanOrEqualConstraint(getScalableEvaluator(), value, getContextLevel());
        addConstraint(constraint);
    }

    @Override
    public void exitLessThanConstraint(PSLGrammarParser.LessThanConstraintContext ctx) {
        ScalableValue value = getValue(ctx.INT(), ctx.time(), ctx.termYear());
        Constraint constraint = new LessThanConstraint(getScalableEvaluator(), value, getContextLevel());
        addConstraint(constraint);
    }

    @Override
    public void exitLessThanOrEqualConstraint(PSLGrammarParser.LessThanOrEqualConstraintContext ctx) {
        ScalableValue value = getValue(ctx.INT(), ctx.time(), ctx.termYear());
        Constraint constraint = new LessThanOrEqualConstraint(getScalableEvaluator(), value, getContextLevel());
        addConstraint(constraint);
    }

    @Override
    public void exitBooleanConstraint(PSLGrammarParser.BooleanConstraintContext ctx) {
        ContextEvaluator evaluator = getContextEvaluator();
        if (evaluator instanceof BooleanContextEvaluator) {
            addConstraint(new BooleanConstraint((BooleanContextEvaluator) evaluator, getContextLevel()));
        } else if (evaluator instanceof TermYearContextEvaluator) {
            addConstraint(new BooleanConstraint((TermYearContextEvaluator) evaluator, getContextLevel()));
        } else {
            assert false : "Couldn't cast to BooleanContextEvaluator or TermYearContextEvaluator";
        }
    }


    // --- Evaluators ---
    private Set<CourseID> getCourseIDSet(PSLGrammarParser.CourseListContext ctx) {
        Set<CourseID> courseIDSet = new HashSet<>();
        ctx.courseID().forEach(courseIDContext -> courseIDSet.add(new CourseID(courseIDContext.getText())));
        return courseIDSet;
    }

    // Numeric

    @Override
    public void exitTotalCourses(PSLGrammarParser.TotalCoursesContext ctx) {
        addEvaluator(new TotalCoursesEvaluator());
    }

    @Override
    public void exitTotalCoursesFromSet(PSLGrammarParser.TotalCoursesFromSetContext ctx) {
        addEvaluator(new TotalCoursesFromSetEvaluator(getCourseIDSet(ctx.courseList())));
    }

    @Override
    public void exitTotalCredits(PSLGrammarParser.TotalCreditsContext ctx) {
        addEvaluator(new TotalCreditsEvaluator());
    }

    @Override
    public void exitCourseList(PSLGrammarParser.CourseListContext ctx) {
        ctx.courseID().forEach(courseIDContext -> parsingContextStack.peek().courseIDS.add(new CourseID(courseIDContext.getText())));
    }

    @Override
    public void exitTotalCreditsFromSet(PSLGrammarParser.TotalCreditsFromSetContext ctx) {
        addEvaluator(new TotalCreditsFromSetEvaluator(parsingContextStack.peek().courseIDS));
    }

    @Override
    public void exitUpperDivisionCourses(PSLGrammarParser.UpperDivisionCoursesContext ctx) {
        addEvaluator(new TotalUpperLevelCoursesEvaluator());
    }

    @Override
    public void exitUpperDivisionCredits(PSLGrammarParser.UpperDivisionCreditsContext ctx) {
        addEvaluator(new TotalUpperLevelCreditsEvaluator());
    }

    @Override
    public void exitMeetingMinutes(PSLGrammarParser.MeetingMinutesContext ctx) {
        addEvaluator(new MeetingMinutesEvaluator());
    }

    @Override
    public void exitNumCoursesWithProfessor(PSLGrammarParser.NumCoursesWithProfessorContext ctx) {
        String professor = ctx.professor().getText();
        addEvaluator(new CoursesWithProfessorEvaluator(professor));
    }

    @Override
    public void exitTimeRange(PSLGrammarParser.TimeRangeContext ctx) {
        Time start = new Time(ctx.time(0).getText());
        Time end = new Time(ctx.time(1).getText());
        parsingContextStack.peek().timeRanges.add(new TimeRange(start, end));
    }

    @Override
    public void exitNumTimeBlocks(PSLGrammarParser.NumTimeBlocksContext ctx) {
        addEvaluator(new NumTimeBlocksEvaluator(10, parsingContextStack.peek().timeRanges));
    }

    @Override
    public void exitTermsInPlan(PSLGrammarParser.TermsInPlanContext ctx) {
        addEvaluator(new NumTermsEvaluator());
    }

    // TermYear

    @Override
    public void exitCourseTermYear(PSLGrammarParser.CourseTermYearContext ctx) {
        addEvaluator(new CourseTermYearEvaluator(new CourseID(ctx.courseID().getText())));
    }

    @Override
    public void exitPlanStart(PSLGrammarParser.PlanStartContext ctx) {
        addEvaluator(new FirstTermEvaluator());
    }

    @Override
    public void exitPlanEnd(PSLGrammarParser.PlanEndContext ctx) {
        addEvaluator(new LastTermEvaluator());
    }

    // Time

    @Override
    public void exitDayStarting(PSLGrammarParser.DayStartingContext ctx) {
        addEvaluator(new WeekdayStartTime());
    }

    @Override
    public void exitDayEnding(PSLGrammarParser.DayEndingContext ctx) {
        addEvaluator(new WeekdayEndTime());
    }

    @Override
    public void exitCourseStart(PSLGrammarParser.CourseStartContext ctx) {
        addEvaluator(new CourseStartTimeEvaluator(new CourseID(ctx.courseID().getText())));
    }

    @Override
    public void exitCourseEnd(PSLGrammarParser.CourseEndContext ctx) {
        addEvaluator(new CourseEndTimeEvaluator(new CourseID(ctx.courseID().getText())));
    }

    // Boolean
    @Override
    public void exitMeetingAtTimeRange(PSLGrammarParser.MeetingAtTimeRangeContext ctx) {
        addEvaluator(new MeetingInTimeRangeEvaluator(parsingContextStack.peek().timeRanges.get(0)));
    }

    @Override
    public void exitCourseBeforeCourse(PSLGrammarParser.CourseBeforeCourseContext ctx) {
        CourseID first = new CourseID(ctx.courseID(0).getText());
        CourseID second = new CourseID(ctx.courseID(1).getText());
        addEvaluator(new CourseBeforeCourseEvaluator(first, second));
    }

    @Override
    public void exitCoursesInSameTerm(PSLGrammarParser.CoursesInSameTermContext ctx) {
        Set<CourseID> courseIDS = new HashSet<>();
        ctx.courseList().courseID().forEach(courseIDContext -> courseIDS.add(new CourseID(courseIDContext.getText())));
        addEvaluator(new CoursesScheduledInSameSemester(courseIDS));
    }
}
