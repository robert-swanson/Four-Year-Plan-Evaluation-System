# PSL Grammar

[TOC]

> The PSL grammar is defined in the [`PSLGrammar.g4`](../java-src/psl/PSLGrammar.g4) ANTLR file and ignores whitespace.

A PSL file is composed of a list of named blocks. Each block defines a set of named priorities (defined in parenthesis) to be optionally used by included preferences. The block then includes a list of `Specification` type statements.

The following shows the starting rules for the grammar:

```
start: (block)+ EOF;
block: NAME priorityList? '{' specification+ '}';
```

The following shows an example PSL file including a block named `student_preferences` and two named priorities: moderately (with a weighting of 2) and strongly (with a weighting of 10).

```
student_preferences (moderately=2.0, strongly=10.0) {
	...
}
```

## Specifications

A specification defines a particular preference or requirement for a degree plan. There are 5 types of specifications:

1. [Requirement Specification](#Requirement-Specification)
2. [Preference Specification](#Preference-Specification)
3. [Specification List](#Specification-List)
4. [Conditional Specification](#Conditional-Specification)
5. [Contextual Specification](#Contextual-Specification)

Requirement and preference specifications end in periods and make up the atomic elements of the entire specification, while lists, conditionals, and contextual serve as special containers for specifications.

A `specification` is defined in ANTLR as:

```
specification:
    requirementSpecification |
    preferenceSpecification |
    specificationList |
    conditionalSpecification |
    contextualSpecification;
```

### Requirement Specification

A requirement specification defines a limitation on the degree plan that must be met for the plan to be considered at all. If any requirement specification evaluates to false, the plan is invalid.

A `requirementSpecification` is defined in ANTLR as:

```
requirementSpecification: REQUIRE NOT? requirableConstraint DOT;
```

For example:

```
require 120 credits.
```

This shows a requirement for a plan to have 120 credits.

### Preference Specification

A preference specification defines a limitation on the degree plan that is not required for the plan, but still desired. While requirements impact a single boolean value describing the plans validity, preference impact a composite percentage value (0%-100%) describing how well the plan meets the desired (but not required) constraints.

By default, all preferences that are evaluated are all averaged together to have an equal impact on the composite score. However, a preference statement can also include a priority (who's values are defined in the block definition) which can modify the weighting of a preference. If no priority is specified, the default weighting is 1.

A `preferenceSpecification` is defined in ANTLR as:

```
preferenceSpecification: PREFER NAME? NOT? constraint DOT;
```

For example:

```
prefer strongly more courses.
```

This shows a preference for a plan to have more courses.

### Specification List

A specification list is a specification that serves as a curly-brace surrounded container for multiple specifications. Anywhere that expects a specification, can also be provided a specification list.

A `specificationList` is defined in ANTLR as:

```
specificationList: ('{' specification* '}');
```

For example:

```
{
	require 120 credits.
	prefer strongly more courses.
}
```

This shows a specification that contains the previous two examples.

### Conditional Specification

A conditional specification allows for  `if`/`else if` /`else` style programming of specifications. Specifically, it defines a set of conditional-specification pairs where only the specification paired to the first condition to evaluate to true will be evaluated. In other words, only on of the contained specifications will actually impact the validity or score of the plan.

A `conditionalSpecification` are defined in ANTLR as:

```
conditionalSpecification: 
	IF condition THEN specification 
	(OTHERWISE_IF condition THEN specification)*
	(OTHERWISE specification)?;
```

[Conditions](#Conditions) will be discussed later, but they contain requirable constraints and support arbitrary Boolean logic.

For example:

```
if taking course "COS-120" then {
    prefer 120 credits.
} otherwise if taking course "COS-121" then {
    prefer more than 120 credits.
} otherwise {
		prefer less than 120 credits.
}
```

This shows a specification that will prefer 120 credits if the plan includes “COS-120”, otherwise it will prefer more than 120 credits if the plan includes “COS-121”, otherwise it will prefer less than 120 credits.

### Contextual Specification

Contextual specifications serve as containers that modify the context in which nested specifications are evaluated. The concept of a context is discussed in further detail in the [context]() section of the evaluation engine page. Essentially, a contextual specification contains a context level, a condition which serves as the context filter, and a specification that will be evaluated under the resulting context.

A `contextualSpecification` is defined in ANTLR as:

```
contextualSpecification: FOR (contextLevel WHERE condition | termYearList | weekdayList) specification;
contextLevel: TERMS|DAYS;
```

For example:

```
for terms where less than 16 credits {
	prefer more upper division courses.
}
```

This shows a specification that will filter each term through the condition, leaving only terms with less than 16 credits. The specification is applied to each term in the context and average together. In this case, each term with less than 16 credits will be encouraged to contain more upper division courses.

## Constraints

Both requirement and preference specifications are composed of constraints. Constraints define a specific limitation on a degree plan that can be mathematically defined as a preference score. 

A subset of constraints, called requirable constraints additionally support the ability to evaluate to a boolean value. Requirement specifications and conditions both need a requirable constraint because they only accept true/false results. However, preferences can accept any constraint.

There are two types of requirable constraints:

1. **Quantifiers** (=,>,<,≥,≤) specify a numeric evaluator for the left side of the (in)equality and a constant value for the right side
2. **Boolean constraints** specify a boolean evaluator

The only constraints that are not requirable are the more constraint and the less constraint. These constraints behave like the inequalities, except that no comparison value is provided. Instead the system will simply seek to maximize the value, and thus the constraint can never be completely fulfilled.

The constraints are defined in ANTLR as:

```
requirableConstraint:
    equalConstraint |
    greaterThanConstraint |
    greaterThanOrEqualConstraint |
    lessThanConstraint |
    lessThanOrEqualConstraint |
    booleanConstraint;
constraint: requirableConstraint |
    moreConstraint |
    lessConstraint;
```

For example, `13 credits` is a requirable constraint, and `more credits` is a non-requirable constraint. The former could be used in conditions, require, or prefer specifications, but the latter could only be used in prefer specifications.

The grammar is designed to be human readable, so the words used to define the constraint depend both on the quantifier used, and the type of value being constrained. Following is the comprehensive list of the constraints and their grammar:

### Equality

```
equalConstraint:
    (INT numericEvaluator) |
    (timeEvaluators AT time) |
    (termYearEvaluators IN termYear);
```

For example:

- Numeric Evaluators: `15 credits`
- Time Evaluators (*at*): `starting at 10:00 AM`
- Term Year Evaluators (*in*): `plan starting in fall 2018`

### Greater Than 

```
greaterThanConstraint:
    (MORE_ THAN INT numericEvaluator) |
    (timeEvaluators AFTER time) |
    (termYearEvaluators AFTER termYear;
```

For example:

- Numeric Evaluators (*more than*): `more than 15 credits`
- Time Evaluators (*after*): `starting after 10:00 AM`
- Term Year Evaluators (*after*): `plan starting after fall 2018`

### Greater Than or Equal To

```
greaterThanOrEqualConstraint:
    (AT LEAST INT numericEvaluator) |
    (timeEvaluators AT OR AFTER time) |
    (termYearEvaluators ON OR AFTER termYear);
```

For example:

- Numeric Evaluators (*at least*): `at least 15 credits`
- Time Evaluators (*at or after*): `starting at or after 10:00 AM`
- Term Year Evaluators (*on or after*): `plan starting on or after fall 2018`

### Less Than 

```
lessThanConstraint:
    (LESS THAN INT numericEvaluator) |
    (timeEvaluators BEFORE time) |
    (termYearEvaluators BEFORE termYear);
```

For example:

- Numeric Evaluators (*less than*): `less than 15 credits`
- Time Evaluators (*before*): `starting before 10:00 AM`
- Term Year Evaluators (*before*): `plan starting before fall 2018`

### Less Than or Equal To

```
lessThanOrEqualConstraint:
    (NO MORE_ THAN INT numericEvaluator) |
    (timeEvaluators AT OR BEFORE time) |
    (termYearEvaluators ON OR BEFORE termYear);
```

For example:

- Numeric Evaluators (*no more than*): `no more than 15 credits`
- Time Evaluators (*at or before*): `starting at or before 10:00 AM`
- Term Year Evaluators (*on or before*): `plan starting on or before fall 2018`

### More

```
moreConstraint:
    MORE_ numericEvaluator |
    timeEvaluators LATER |
    termYearEvaluators LATER;
```

For example:

- Numeric Evaluators (*more*): `more credits`
- Time Evaluators (*later*): `starting later`
- Term Year Evaluators (*later*): `plan starting later`

### Less

```
lessConstraint:
    LESS numericEvaluator |
    timeEvaluators EARLIER |
    termYearEvaluators EARLIER;
```

For example:

- Numeric Evaluators (*less*): `less credits`
- Time Evaluators (*earlier*): `starting earlier`
- Term Year Evaluators (*earlier*): `plan starting earlier`

Here is a summary of the preceding information:

| Constraint | Numeric (prefix) | Time (middle)  | TermYear (middle) |
| ---------- | ---------------- | -------------- | ----------------- |
| $=$        | (No prefix)      | `at`           | `in`              |
| $>$        | `more than`      | `after`        | `after`           |
| $\ge$      | `at least`       | `at or after`  | `on or after`     |
| $<$        | `less than`      | `before`       | `before`          |
| $\le$      | `no more than`   | `at or before` | `on or before`    |
| More       | `more`           | `later`        | `later`           |
| Less       | `less`           | `earlier`      | `earlier`         |



## Conditions

Conditions are used in conditional and contextual specifications to evaluate a context and provide a boolean value. Conditions are composed of requirable constraints and can be combined with Boolean logic (and, or, not).

Conditions are defined in ANTLR as:

```
condition: requirableConstraint |
    OPEN_PAREN condition CLOSE_PAREN |
    OPEN_PAREN condition AND condition CLOSE_PAREN |
    OPEN_PAREN condition OR condition CLOSE_PAREN |
    NOT condition;
```

For example:

```
if (taking course "COS-120" and not taking course "COS-109") then {
	prefer taking course "COS-143".
}
```

This conditional specification defines a preference to take “COS-143” provided that they are taking “COS-120” but not “COS-109”.

## Evaluators

Context evaluators represent attributes of the degree plan that are restricted by constraints. More specifically, they represent attributes of a context, which is passed in at evaluation time, producing a `Result` object.

Following is the ANTLR definition for the different evaluators:

```
numericEvaluator:
	totalCredits | 
	totalCreditsFromSet | 
	upperDivisionCredits | 
	totalCourses | 
	totalCoursesFromSet | 
	upperDivisionCourses | 
	meetingMinutes | 
	numCoursesWithProfessor | 
	numTimeBlocks | 
	termsInPlan;
termYearEvaluators: 
	courseTermYear | 
	planStart | 
	planEnd;
timeEvaluators: dayStarting |
	dayEnding | 
	courseStart | 
	courseEnd;
booleanEvaluators: 
	meetingAtTimeRange | 
	courseBeforeCourse | 
	coursesInSameTerm | 
	termExists;
```

Context evaluators can be describe by a couple of characteristics:

1. Return Value Type
   1. Numeric
   2. Term Year
   3. Time
   4. Boolean
2. Context Behavior
   1. **Adaptive**: Returns Plan/Terms/Days result based on current context
   2. **Single**: Always returns a single (plan) result regardless of context
   3. **Weekday**: Always returns a value for each weekday in context

Following is a comprehensive list of all the supported context evaluators and their attributes:

| `ContextEvaluator`                | Return Value Type   | Context Behavior | Description                                                  |
| --------------------------------- | ------------------- | ---------------- | ------------------------------------------------------------ |
| `courses`                         | Numeric             | Adaptive         | The total number of courses in context                       |
| `courses from <course list>`      | Numeric             | Adaptive         | The total number of courses from the list that are in context |
| `credits`                         | Numeric             | Adaptive         | The total number of credits in context                       |
| `credits from <course list>`      | Numeric             | Adaptive         | The total number of credits from the list that are in context |
| `upper division courses`          | Numeric             | Adaptive         | The total number of upper division courses that are in context |
| `upper division credits`          | Numeric             | Adaptive         | The total number of upper division credits that are in context |
| `meeting minutes`                 | Numeric             | Adaptive         | The sum of all the meetings from the weekdays in context     |
| `courses with <professor>`        | Numeric             | Adaptive         | The sum of all the courses taken with                        |
| `time blocks`                     | Numeric             | Weekday          | The number of blocks of contiguous meetings in a weekday context (where one meeting ends no more than 10 minutes before the next one starts) |
| `terms`                           | Numeric             | Single           | The number of terms in context                               |
| `taking course <course>`          | TermYear, Boolean   | Single           | When (or if) the course is scheduled                         |
| `plan starting`                   | TermYear, (Boolean) | Single           | The first term in the degree plan                            |
| `plan ending`                     | TermYear, (Boolean) | Single           | The last term in the degree plan                             |
| `starting`                        | Time                | Weekday          | The time the first class starts in a weekday context         |
| `ending`                          | Time                | Weekday          | The time the last class ends in a weekday context            |
| `<course> starting`               | Time                | Single           | The time that the course starts (most common if multiple)    |
| `<course> ending`                 | Time                | Single           | The time that the course ends (most common if multiple)      |
| `meeting at <time range>`         | Boolean             | Weekday          | True if there is a meeting that intersects with the time range in a weekday context |
| `taking <course> before <course>` | Boolean             | Single           | True if the first course is schedule before the second       |
| `taking <course list> together`   | Boolean             | Single           | True if every course in the list (that's scheduled at all) is scheduled for the same term |

