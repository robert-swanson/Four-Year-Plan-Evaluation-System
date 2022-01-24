package preferences.json;

import com.google.gson.*;
import preferences.specification.FullSpecification;
import preferences.specification.Specification;

import java.lang.reflect.Type;

public class FullSpecificationDeserializer implements JsonDeserializer<FullSpecification> {
    @Override
    public FullSpecification deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Specification specification = jsonDeserializationContext.deserialize(jsonObject.get("specification"), Specification.class);
        String name = jsonObject.get("name").getAsString();
        return new FullSpecification(specification, name);
    }
}
