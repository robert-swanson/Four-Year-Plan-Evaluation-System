package preferences.json;

// Cite: https://stackoverflow.com/a/9106351/6658911

import com.google.gson.*;

import java.lang.reflect.Type;

public class PropertyBasedInterfaceMarshal implements JsonSerializer<Object>, JsonDeserializer<Object> {

    private static final String CLASS_META_KEY = "class";

    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObj = jsonElement.getAsJsonObject();
        String className = jsonObj.get(CLASS_META_KEY).getAsString();
        try {
            Class<?> clz = Class.forName(className);
            return jsonDeserializationContext.deserialize(jsonElement, clz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
        jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName());
        return jsonEle;
    }

}