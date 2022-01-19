package objects.plan;

import com.google.gson.*;
import exceptions.JSONParseException;
import exceptions.PlanException;
import objects.Linkable;
import objects.Link;
import objects.misc.SectionID;
import objects.misc.TermYear;
import objects.offerings.CourseOffering;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;

public class PlanTerm implements Linkable {
    private final ArrayList<SectionID> sections;
    private final transient TermYear termYear;

    public PlanTerm(ArrayList<SectionID> sections, TermYear termYear) {
        this.sections = sections;
        this.termYear = termYear;
    }

    public static class Serializer implements JsonSerializer<PlanTerm> {
        @Override
        public JsonElement serialize(PlanTerm planTerm, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonArray array = new JsonArray();
            for (SectionID sectionID : planTerm.sections) {
                array.add(sectionID.toString());
            }
            return array;
        }
    }

    public static class Deserializer implements JsonDeserializer<PlanTerm> {
        @Override
        public PlanTerm deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            try {
                JsonObject object = jsonElement.getAsJsonObject();
                TermYear termYear = new TermYear(object.get(Plan.TERM_YEAR).getAsString());

                JsonArray array = object.get(Plan.TERMS).getAsJsonArray();
                ArrayList<SectionID> sections = new ArrayList<>();

                for (JsonElement element : array) {
                    sections.add(new SectionID(element.getAsString()));
                }
                return new PlanTerm(sections, termYear);
            } catch (JSONParseException e) {
                throw new JsonParseException(e.getMessage());
            }
        }
    }

    private transient Link link;
    private transient LinkedList<CourseOffering> courseOfferings;
    @Override
    public void link(Link l) throws PlanException {
        link = l;
        try {
            courseOfferings = new LinkedList<>();
            for (SectionID sectionID: sections) {
                courseOfferings.add(link.getOfferings().getOfferings(termYear).getSectionOffering(sectionID));
            }
        } catch (Exception e) {
            throw new PlanException(String.format("Linking sections for term '%s' in plan\n\t-> %s", termYear, e));
        }
    }

    public LinkedList<CourseOffering> getCourseOfferings() {
        return courseOfferings;
    }


    public TermYear getTermYear() {
        return termYear;
    }
}
