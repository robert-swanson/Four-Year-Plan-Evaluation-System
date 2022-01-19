package objects.offerings;

import com.google.gson.*;
import exceptions.OfferingException;
import objects.Linkable;
import objects.misc.CourseID;
import objects.Link;
import objects.misc.SectionID;
import objects.misc.TermYear;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;

public class CourseOfferings implements Linkable {
    private LinkedList<CourseOffering> offerings;

    public CourseOfferings(LinkedList<CourseOffering> offerings) {
        this.offerings = offerings;
    }

    public static class Serializer implements JsonSerializer<CourseOfferings> {
        @Override
        public JsonElement serialize(CourseOfferings offerings, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonArray array = new JsonArray();
            for (CourseOffering offering : offerings.offerings) {
                array.add(jsonSerializationContext.serialize(offering, CourseOffering.class));
            }
            return array;
        }
    }

    public static class Deserializer implements JsonDeserializer<CourseOfferings> {
        @Override
        public CourseOfferings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonArray array = jsonElement.getAsJsonArray();
            LinkedList<CourseOffering> offerings = new LinkedList<>();
            for (JsonElement offering : array) {
                offerings.add(jsonDeserializationContext.deserialize(offering, CourseOffering.class));
            }
            return new CourseOfferings(offerings);
        }
    }

    private transient Link link;
    private transient HashMap<SectionID, CourseOffering> sectionOfferings;
    public transient CourseID courseID; // set by TermOfferings.link()
    public transient TermYear termYear; // set by TermOfferings.link()
    @Override
    public void link(Link l) throws OfferingException { // called by TermOfferings
        link = l;
        sectionOfferings = new HashMap<>();
        for (CourseOffering offering: offerings) {
            offering.courseID = courseID;
            offering.termYear = termYear;
            offering.link(l);
            sectionOfferings.put(offering.getCrn(), offering);
        }
    }

    public CourseOffering getSectionOffering(SectionID sectionID) throws OfferingException {
        if (sectionOfferings.containsKey(sectionID)) {
            return sectionOfferings.get(sectionID);
        } else {
            throw new OfferingException(String.format("No section with id '%s' is being offered for this course in this term", sectionID));
        }
    }

    public LinkedList<CourseOffering> getOfferings() {
        return offerings;
    }
}
