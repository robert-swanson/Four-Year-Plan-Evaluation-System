package objects.plan;

import com.google.gson.*;
import exceptions.JSONParseException;
import exceptions.PlanException;
import objects.Linkable;
import objects.Link;
import objects.misc.CourseID;
import objects.misc.TermYear;
import objects.offerings.CourseOffering;
import objects.offerings.TermOfferings;

import java.lang.reflect.Type;
import java.util.*;

public class Plan implements Linkable {
    private double score;
    private String comments;
    public final String id;
    private LinkedHashMap<TermYear, PlanTerm> terms;

    public Iterable<PlanTerm> getTerms(){
        return terms.values();
    }
    public LinkedHashMap<TermYear, PlanTerm> getTermsMap() { return terms; }

    public Plan(double score, String comments, LinkedHashMap<TermYear, PlanTerm> terms, String id) {
        this.score = score;
        this.comments = comments;
        this.terms = terms;
        this.id = id;
    }

    public static final String SCORE = "score";
    public static final String COMMENTS = "comments";
    public static final String TERM_YEAR = "termYear";
    public static final String TERMS = "terms";

    public static class Serializer implements JsonSerializer<Plan> {
        @Override
        public JsonElement serialize(Plan plan, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject object = new JsonObject();
            object.addProperty(SCORE, plan.score);
            object.addProperty(COMMENTS, plan.comments);
            PlanTerm.Serializer planTermSerializer = new PlanTerm.Serializer();
            JsonObject terms = new JsonObject();
            for (Map.Entry<TermYear,PlanTerm> entry : plan.terms.entrySet()) {
                terms.add(entry.getKey().toString(), planTermSerializer.serialize(entry.getValue(), PlanTerm.class, jsonSerializationContext));
            }
            object.add(TERMS, terms);
            return object;
        }
    }

    public static class Deserializer implements JsonDeserializer<Plan> {
        @Override
        public Plan deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            PlanTerm.Deserializer planTermDeserializer = new PlanTerm.Deserializer();
            LinkedHashMap<TermYear, PlanTerm> terms = new LinkedHashMap<>();
            double score = -1.0;
            if (object.has(SCORE)){
                score = object.get(SCORE).getAsDouble();
            }
            String comments = object.get(COMMENTS).getAsString();
            try {
                for (Map.Entry<String, JsonElement> entry : object.get(TERMS).getAsJsonObject().entrySet()) {
                    TermYear termYear = new TermYear(entry.getKey());

                    JsonObject termObject = new JsonObject();
                    termObject.addProperty(TERM_YEAR, termYear.toString());
                    termObject.add(TERMS, entry.getValue());
                    PlanTerm planTerm = planTermDeserializer.deserialize(termObject, TermOfferings.class, jsonDeserializationContext);
                    terms.put(termYear, planTerm);
                }
            } catch (JSONParseException e) {
                throw new JsonParseException(e.getMessage());
            }
            String id = object.get("id").getAsString();
            return new Plan(score, comments, terms, id);
        }
    }

    private transient HashMap<CourseID, ArrayList<CourseOffering>> courseMap;
    private transient Link link;
    @Override
    public void link(Link l) throws PlanException {
        courseMap = new HashMap<>();
        link = l;
        for (PlanTerm planTerm: terms.values()) {
            planTerm.link(l);

            planTerm.getCourseOfferings().forEach(courseOffering -> {
                CourseID courseID = courseOffering.getCourse().getCourseID();
                if (!courseMap.containsKey(courseID)) {
                    courseMap.put(courseID, new ArrayList<>());
                }
                courseMap.get(courseID).add(courseOffering);
            });
        }
    }

    public HashMap<CourseID, ArrayList<CourseOffering>> getCourseMap() {
        return courseMap;
    }

    public CourseOffering getOffering(CourseID courseID) {
        if (courseMap.containsKey(courseID)) {
            return courseMap.get(courseID).get(0);
        }
        return null;
    }

    public double getScore() {
        return score;
    }

    public String getComments() {
        return comments;
    }

    public static class OfferingIterator implements Iterator<CourseOffering>, Iterable<CourseOffering> {
        private Plan plan;
        private Iterator<PlanTerm> termIterator;
        private Iterator<CourseOffering> courseOfferingIterator;
        private boolean hasNext = true;

        public OfferingIterator(Plan plan) {
            this.plan = plan;
            termIterator = plan.terms.values().iterator();

            while((courseOfferingIterator == null || !courseOfferingIterator.hasNext()) && termIterator.hasNext()) {
                courseOfferingIterator = termIterator.next().getCourseOfferings().iterator();
            }

            if (!termIterator.hasNext()) {
                hasNext = false;
            }
        }
        @Override
        public boolean hasNext() {
            return hasNext;
        }

        @Override
        public CourseOffering next() {
            CourseOffering offering = courseOfferingIterator.next();
            while (!courseOfferingIterator.hasNext() && termIterator.hasNext()) {
                courseOfferingIterator = termIterator.next().getCourseOfferings().iterator();
            }
            if (!courseOfferingIterator.hasNext()) {
                hasNext = false;
            }
            return offering;
        }

        @Override
        public Iterator<CourseOffering> iterator() {
            return this;
        }
    }

    public Iterable<CourseOffering> getCourseOfferingIterable() {
        return new OfferingIterator(this);
    }
}
