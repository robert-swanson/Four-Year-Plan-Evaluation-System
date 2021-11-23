package objects.misc;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;

public class CourseID {
    final String id;

    public CourseID(String id) {
        this.id = id;
    }

    public static class Serializer implements JsonSerializer<CourseID> {
        @Override
        public JsonElement serialize(CourseID courseID, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(courseID.id);
        }
    }

    public static class Deserializer implements JsonDeserializer<CourseID> {
        @Override
        public CourseID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new CourseID(jsonElement.getAsString());
        }
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseID courseID = (CourseID) o;
        return Objects.equals(id, courseID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
