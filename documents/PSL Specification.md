# PSL Specification

## Misc

- [ ] Prerequisite/Corequisite Checks on Plan Loading (`plan.validatePrereqs()`)
- [ ] Meeting Time Conflict Check on Plan Loading (`plan.validateMeetingNoConflicts()`)
- [ ] `strictly prefer more than 3 credits` | `prefer strictly more than 3 credits`

## Contexts:

- Full Plan
- Terms
- Weekday

## Result

- [ ] PlanResult
- [ ] TermsResult
- [ ] WeekdayResult
- [ ] 

## Value

- [x] Text
- [ ] Boolean
- [ ] List

### Scalable Values

- [x] Numeric
- [ ] TermYear
- [x] Time

## Evaluators:

### Numeric

- [x] Total Courses
- [x] Total Courses from Set (Set)
- [x] Total Credits 
- [x] Total Credits from Set (Set)
- [x] Upper Division Courses
- [x] Upper Division Credits
- [x] Meeting Minutes

### [TermYear]

- [ ] What Terms Are Courses Scheduled (Courses)
  - [ ] **Full Plan:** (for each course) eg: `require taking "COS-120", "COS-121" before fall 2019`.
- [ ] Professors TermYears  (Professors) [*only full plan context*]

### Time

- [x] Start Time
- [x] End Time
- [ ] What Time Are Courses Scheduled (Courses)
  - [ ] **Any Context:** [Time], most common start time for class in context eg: `prefer taking "COS-120" at 10:00 AM. `

## Constraints:

- [x] Equal ← Any
- [x] Greater Than ← Scalar
- [x] Greater Than or Equal To ← Scalar
- [x] Less Than ← Scalar
- [x] Less Than or Equal To ← Scalar
- [ ] Exactly
- [ ] InList ← Any

---

- [ ] IsTrue ← Boolean
- [ ] IsNotNull ← Any

## Specifications:

- [x] Preference
- [x] Requirement
- [x] List
- [x] Conditional
- [x] Contextual
- [x] Invert

## Example Evaluator Statements:

```
// Numeric
require 4 courses.
require 4 courses from "COS 120", "COS 121", "COS 265", "COS 243".
require 16 credits.
require 16 credits from "COS 120", "COS 121", "COS 265", "COS 243".
require 4 upper division courses.
require 4 upper division credits.
require 4 meeting hours.

// Numeric
require taking "COS 120", "COS 121" in fall 2018.
require taking "Dr. Brandle", "Dr. Giesler" in fall 2018.

// Time
require start at 8:00 AM.
require end at 3:00 PM.
require average start at 8:00 AM.
require average end at 3:00 PM.

// Boolean
require meeting at 11:00 AM
```

## Example Constraint Statements:

```
require 4 courses.
require more than 4 courses.
require at least 4 courses.
require less than 4 courses.
require no more than 4 courses.
require meeting at 11:00.	// is true
require taking "COS 120", "COS 121". // is not null
```

```
require starting after 8:00.				// greater than
require starting before 8:00.				// less than
require starting on or before 8:00.	// less than equal
require starting on or after 8:00.	// greater than equal
```

