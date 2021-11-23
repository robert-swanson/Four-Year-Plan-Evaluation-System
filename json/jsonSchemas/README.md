# Schema Summaries

## Catalog

- `catalog-year`: The catalog year this represents (eg ‘2021-2022’)
- `departments`: array of department objects
  - `name`: The name of the department
  - `description`: (*optional*) A description of the department
  - `courses`: array of course objects
    - `course-id`: A unique string identifier for this course (recommended format ‘<prefix> <number>’)
    - `name`: The full name of the course 
    - `prefix`: The three letter prefix for this course
    - `number`: The course number
    - `description`: (*optional*)  A description of the course
    - `prerequisites`: (*optional*) A list of `course-id` strings for any prerequisites for this course
    - `offering-pattern`: (*optional*) A enum string describing the pattern of this course's known offering
    - `tags`: (*optional*) A list describing tags relating to this course (such as honors, cc, or sp)

## Offerings

- `catalog-year`: The catalog year this represents (eg ‘2021-2022’)
- `<term>`: A term mapped to a term object
  - `<course-id>`: A `course-id` mapped to a list of course offering objects
    - `crn`: A course reference number uniquely identifying this course offering within this semester
    - `section`: The section number for this course offering
    - `type`: Either ‘lecture’ or ‘lab’ describing if this section fulfills the lecture or lab requirement for this course
    - `credits`: The number of credits this section fulfills
    - `professors`: A list of professor names for this section
    - `location`: An object describing where this section meets
      - `building`: String describing building
      - `room-number`: String describing room number
    - `num-enrolled`: (*optional*) Number of students enrolled in section
    - `max-enrollment`: (*optional*) Maximum number of students allowed in this section
    - `start-date`: (*optional*) For courses that start after the beginning of the semester, formatted ‘yyyy-mm-dd’
    - `end-date`: (*optional*) For courses that end before the end of the semester, formatted ‘yyyy-mm-dd’
    - `meetings` A list of meeting objects
      - `days`: A list of days of the week that this meeting object applies to (eg ‘Monday’)
      - `start-time`: The start time for this meeting (eg ’12:00 PM’)
      - `end-time`: The end time for this meeting (eg ’12:50 PM’)

## Plan

- `plans`: A list of plan objects
  - `score`: (*optional*) A number between 0 and 100 describing the percentage score of this plan
  - `comments`: (*optional*) notes relating to this plan
  - `termsMasterContext`: a list of term objects
    - `year`: The year for this term
    - `term`: The term this object is describing
    - `sections`: A list of course reference numbers (`crn`) describing the sections to enroll for this term according to this plan  