package objects.misc;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;

public class SectionID {
    final String id;

    public SectionID(String id) {
        this.id = id;
    }

    public static class Serializer implements JsonSerializer<SectionID> {
        @Override
        public JsonElement serialize(SectionID sectionID, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(sectionID.id);
        }
    }

    public static class Deserializer implements JsonDeserializer<SectionID> {
        @Override
        public SectionID deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new SectionID(jsonElement.getAsString());
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
        SectionID sectionID = (SectionID) o;
        return Objects.equals(id, sectionID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
