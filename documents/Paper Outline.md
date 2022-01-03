# PSL-An Expert System to Evaluate Degree Plans

*[7.5 pages]*

1. Introduction *[1.25 pages]*

   1. Motivation
   2. PSL Summary
   3. Lit Survey 

2. PSL Prototype (*mention when things changed in final PSL*) [*2 pages*]

   1. Inputs/Outputs  ✔

   2. Early PSL Spec

      1. Requirements ✔
      2. Preferences ✔
      3. Condition Blocks
      4. Context Blocks
      5. (Requirable) Constraints
      6. Conditions

   3. Scoring ✔

      1. > Idea: graph out a particular spec’s impact on score

   4. Motivation (unified system)

3. PSL Design [*3 pages*]

   1. ✔ Summary
      1. ✔ Json parsing, objects, linking
   2. Grammar (modular)
      1. ✔ Specification
         1. ✔ Requirement/Preference
         2. ✔ List
         3. ✔ Conditional
            1. ✔ Condition
         4. ✔ Contextual
      2. ✔ Constraints
      3. ✔ Evaluators
         1. ✔numeric
         2. ✔ TermYear
         3. ✔ time
         4. ✔ boolean
   3. Evaluation Engine
      1. ✔ Design Overview
         1. ✔ Use explanation, diagrams
      2. ✔ Specifications → Score
         1. ✔ Types
      3. ✔ (Requireable) Constraints → double/boolean
      4. ✔ Contexts
         1. ✔ Levels
         2. ✔ Iterables\
      5. ✔ Evaluator → Result
         1. ✔ Plan, Terms, Days
         2. ✔ Scoring same as prototype
         3. ✔ Value
      6. ✔ Explanation
   
4. Application: Taylor Data [*.5 pages*]

   1. File generation
   2. Results
   3. How Result changes with modified plan/PSL
   4. Performance?

5. Future Work *[.5 pages]*

   1. More evaluators
   2. More robust explanation
   3. Full debugging
   4. Plan Generation
      1. GA
      2. Constraint based search

6. Conclusion *[.25 pages]*