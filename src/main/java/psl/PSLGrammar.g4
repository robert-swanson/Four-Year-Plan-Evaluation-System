grammar PSLGrammar;

//start: block* EOF;
start: (globalImport|comment)* (comment|block)* EOF;
block: NAME priorityList? '{' (localImport|comment)* (comment|specification)* '}';

globalImport: USE NAME DOT;
localImport: USE NAME DOT;
USE: 'use';

comment: '[['~(']' | '_')*?']]';

// Priority
priority: NAME EQUALS (INT|FLOAT);
priorityList: '(' priority (',' priority)* ')';
PRIORITY: 'priority';
EQUALS: '=';

// --- Specifications ---
specification:
    requirementSpecification |
    preferenceSpecification |
    specificationList |
    conditionalSpecification |
    contextualSpecification |
    comment;

// Requirements
requirementSpecification: REQUIRE NOT? requirableConstraint DOT;
REQUIRE: 'require';

// Preferences
preferenceSpecification: PREFER NAME? NOT? constraint DOT;
PREFER: 'prefer';

// Specification List
specificationList: ('{' specification* '}');

// Conditional Specification
conditionalSpecification: IF condition THEN specification (OTHERWISE_IF condition THEN specification)* (OTHERWISE specification)?;
IF: 'if';
THEN: 'then';
OTHERWISE_IF: 'otherwise if';
OTHERWISE: 'otherwise';

// Contextual Specification
contextualSpecification: FOR (contextLevel WHERE condition | termYearList | weekdayList) specification;

termYearList: termYear (',' termYear)*;
weekdayList: weekday (',' weekday)*;
weekday: 'sundays' | 'mondays' | 'tuesdays' | 'wednesdays' | 'thursdays' | 'fridays' | 'saturdays';

contextLevel: TERMS|DAYS;
FOR: 'for';
PLAN: 'plan';
TERMS: 'terms';
DAYS: 'days';
WHERE: 'where';



// --- Condition ---
condition: requirableConstraint |
    OPEN_PAREN condition CLOSE_PAREN |
    OPEN_PAREN condition AND condition CLOSE_PAREN |
    OPEN_PAREN condition OR condition CLOSE_PAREN |
    NOT condition
    ;

// --- Constraint ---
// Requireable Constraint
requirableConstraint:
    equalConstraint |
    greaterThanConstraint |
    greaterThanOrEqualConstraint |
    lessThanConstraint |
    lessThanOrEqualConstraint |
    booleanConstraint
    ;

equalConstraint:
    (INT numericEvaluator) |
    (timeEvaluators AT time) |
    (termYearEvaluators IN termYear)
    ;

greaterThanConstraint:
    (MORE_ THAN INT numericEvaluator) |
    (timeEvaluators AFTER time) |
    (termYearEvaluators AFTER termYear)
    ;

greaterThanOrEqualConstraint:
    (AT LEAST INT numericEvaluator) |
    (timeEvaluators AT OR AFTER time) |
    (termYearEvaluators ON OR AFTER termYear)
    ;

lessThanConstraint:
    (LESS THAN INT numericEvaluator) |
    (timeEvaluators BEFORE time) |
    (termYearEvaluators BEFORE termYear)
    ;

lessThanOrEqualConstraint:
    (NO MORE_ THAN INT numericEvaluator) |
    (timeEvaluators AT OR BEFORE time) |
    (termYearEvaluators ON OR BEFORE termYear)
    ;

booleanConstraint: booleanEvaluators;

// Not Necesarily Requireable Constraints
constraint: requirableConstraint |
    moreConstraint |
    lessConstraint
    ;

moreConstraint:
    MORE_ numericEvaluator |
    timeEvaluators LATER |
    termYearEvaluators LATER;

lessConstraint:
    LESS numericEvaluator |
    timeEvaluators EARLIER |
    termYearEvaluators EARLIER;

// --- Evaluators ---

numericEvaluator:
    totalCredits | totalCreditsFromSet | upperDivisionCredits | totalCourses |
    totalCoursesFromSet | upperDivisionCourses | meetingMinutes |
    numCoursesWithProfessor | numTimeBlocks | termsInPlan ;

totalCourses: COURSES;
totalCoursesFromSet: COURSE_FROM courseList;
totalCredits: CREDITS;
totalCreditsFromSet: CREDITS FROM courseList;
upperDivisionCourses: UPPER_DIVISION COURSES;
upperDivisionCredits: UPPER_DIVISION CREDITS;
meetingMinutes: MEETING MINUTES;
numCoursesWithProfessor: COURSES WITH professor;
numTimeBlocks: TIME_BLOCKS (RESERVING timeRangeList)?;
termsInPlan: TERMS;

COURSE_FROM: 'courses from';
TIME_BLOCKS: 'time blocks';
RESERVING: 'reserving';

// Term Year Evaluators
termYearEvaluators: courseTermYear | planStart | planEnd;

courseTermYear: TAKING COURSE '"' courseID '"';
planStart: PLAN STARTING;
planEnd: PLAN ENDING;

// Time Evaluators
timeEvaluators: dayStarting | dayEnding | courseStart | courseEnd;

dayStarting: STARTING;
dayEnding: ENDING;
courseStart: '"' courseID '"' STARTING;
courseEnd: '"' courseID '"' ENDING;

// Boolean Evaluators
booleanEvaluators: meetingAtTimeRange | courseBeforeCourse | coursesInSameTerm | termExists;

meetingAtTimeRange: MEETING AT timeRange;
courseBeforeCourse: TAKING '"' courseID '"' BEFORE '"' courseID '"';
coursesInSameTerm: TAKING courseList TOGETHER;
termExists: termYearEvaluators;

// Misc
DOT: '.';
OPEN_PAREN: '(';
CLOSE_PAREN: ')';

IN: 'in';
AT: 'at';
ON: 'on';

TAKING: 'taking';
MEETING: 'meeting';
HOURS: 'hours';
MINUTES: 'minutes';
CREDITS: 'credits';
COURSES: 'courses';
COURSE: 'course';
PROFESSOR: 'professor';
UPPER_DIVISION: 'upper division';

STARTING: 'starting';
ENDING: 'ending';
FROM: 'from';
MORE_: 'more';
LESS: 'less';
AFTER: 'after';
BEFORE: 'before';
THAN: 'than';
LEAST: 'least';
WITH: 'with';
TOGETHER: 'together';
LATER: 'later';
EARLIER: 'earlier';

NO: 'no';
NOT: 'not';
AND: 'and';
OR: 'or';

INT: [0-9]+;
FLOAT: INT '.' INT;
DIGIT: [0-9];
CHAR: [^"];
UPPER: [A-Z];

term: 'fall' | 'winter' | 'jterm' | 'spring' | 'summer';
termYear: term INT;
courseID:  COURSE_PREFIX '-' INT COURSE_POSTFIX?;
COURSE_POSTFIX: UPPER;
COURSE_PREFIX: UPPER+;
courseList: '"' courseID '"' (',' '"' courseID '"')*;
professor: '"'NAME+'"';
time: INT ':' INT  ('AM' | 'PM');
timeRange: time '-' time;
timeRangeList: timeRange (',' timeRange)*;


NAME: ([a-z]|UPPER)('_'|[a-z0-9]|UPPER)*;
WS : [ \t\n]+ -> skip ;  // tells ANTLR to ignore these

