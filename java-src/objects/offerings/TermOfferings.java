package objects.offerings;

import com.google.gson.*;
import exceptions.OfferingException;
import objects.Linkable;
import objects.misc.CourseID;
import objects.Link;
import objects.misc.SectionID;
import objects.misc.TermYear;

import java.lang.reflect.Type;
import java.util.*;

public class TermOfferings implements Linkable {
    private LinkedHashMap<CourseID, CourseOfferings> coursesOfferings;

    public TermOfferings(LinkedHashMap<CourseID, CourseOfferings> coursesOfferings) {
        this.coursesOfferings = coursesOfferings;
    }

    public static class Serializer implements JsonSerializer<TermOfferings> {
        @Override
        public JsonElement serialize(TermOfferings offerings, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject object = new JsonObject();
            CourseOfferings.Serializer courseOfferingsSerializer = new CourseOfferings.Serializer();
            for (CourseID courseID : offerings.coursesOfferings.keySet()) {
                object.add(courseID.toString(), courseOfferingsSerializer.serialize(offerings.coursesOfferings.get(courseID), CourseOfferings.class, jsonSerializationContext));
            }
            return object;
        }
    }

    public static class Deserializer implements JsonDeserializer<TermOfferings> {
        @Override
        public TermOfferings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();
            LinkedHashMap<CourseID, CourseOfferings> courses = new LinkedHashMap<>();
            CourseOfferings.Deserializer courseOfferingsDeserializer = new CourseOfferings.Deserializer();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                CourseID courseID = new CourseID(entry.getKey());
                CourseOfferings course = courseOfferingsDeserializer.deserialize(entry.getValue(), CourseOfferings.class, jsonDeserializationContext);
                courses.put(courseID, course);
            }
            return new TermOfferings(courses);
        }
    }

    public CourseOfferings getCourseOfferings(CourseID id) {
        return coursesOfferings.get(id);
    }

    public LinkedHashMap<CourseID, CourseOfferings> getCoursesOfferings() {
        return coursesOfferings;
    }

    private transient Link link;
    private transient LinkedHashMap<SectionID, CourseOffering> sectionOfferings;
    private transient HashMap<SectionID, CourseID> sectionToCourseIDs;

    public  transient TermYear termYear; // set by Offerings.link()
    @Override
    public void link(Link l) throws OfferingException { // called by Offerings
        link = l;
        sectionOfferings = new LinkedHashMap<>();
        sectionToCourseIDs = new HashMap<>();
        Iterator<Map.Entry<CourseID, CourseOfferings>> iterator = coursesOfferings.entrySet().iterator();
        ArrayList<CourseID> removedCourses = new ArrayList<>();
        while (iterator.hasNext()) {
            Map.Entry<CourseID, CourseOfferings> entry = iterator.next();
            CourseID courseID = entry.getKey();
            CourseOfferings offerings = entry.getValue();
            offerings.courseID = courseID;
            offerings.termYear = termYear;
            try {
                offerings.link(l);
                for (CourseOffering offering : offerings.getOfferings()) {
                    sectionOfferings.put(offering.getCrn(), offering);
                    sectionToCourseIDs.put(offering.getCrn(), courseID);
                }
            } catch (OfferingException e) {
                iterator.remove();
                removedCourses.add(courseID);
            }
        }
//        if (!removedCourses.isEmpty()) {
//            System.err.printf("Removed %d course offerings for %s for courses that couldn't be found in the catalog: %s\n", removedCourses.size(), termYear, removedCourses);
//        }
    }

    public CourseOffering getSectionOffering(SectionID sectionID) throws OfferingException {
        if (sectionOfferings.containsKey(sectionID)) {
            return sectionOfferings.get(sectionID);
        } else {
            throw new OfferingException(String.format("There is no section offering with id '%s' being offered in this term", sectionID.toString()));
        }
    }

    public CourseID getCourseForSection(SectionID sectionID) {
        return sectionToCourseIDs.get(sectionID);
    }
}
