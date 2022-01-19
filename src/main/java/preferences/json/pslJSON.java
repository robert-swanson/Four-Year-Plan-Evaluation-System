package preferences.json;

public class pslJSON {
    private static final String CLASS_META_KEY = "class";



//   private static JsonElement serializeAbstractClass(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
//       JsonElement specificationElement = jsonSerializationContext.serialize(object, object.getClass());
//       specificationElement.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName());
//       return specificationElement;
//   }
//
//    private static Specification deseralizeAbstractClass(JsonElement jsonElement, JsonDeserializationContext jsonDeserializationContext) {
//        JsonObject jsonObj = jsonElement.getAsJsonObject();
//        String className = jsonObj.get(CLASS_META_KEY).getAsString();
//        try {
//            Class<?>  classForName = Class.forName(className);
//            return jsonDeserializationContext.deserialize(jsonElement, classForName);
//        } catch (ClassNotFoundException e) {
//            throw new JsonParseException(e);
//        }
//    }
//
//    public static class SpecificationSerializer implements JsonSerializer<Specification> {
//        @Override
//        public JsonElement serialize(Specification specification, Type type, JsonSerializationContext jsonSerializationContext) {
//            return null;
//        }
//    }
//
//    public static class SpecificationDeserializer implements JsonDeserializer<Specification> {
//        @Override
//        public Specification deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            return deseralizeAbstractClass(jsonElement, jsonDeserializationContext);
//        }
//    }
//
//    public static class ConstraintSerializer implements JsonSerializer<Specification> {
//        @Override
//        public JsonElement serialize(Specification specification, Type type, JsonSerializationContext jsonSerializationContext) {
//            JsonElement specificationElement = jsonSerializationContext.serialize(specification, specification.getClass());
//            specificationElement.getAsJsonObject().addProperty(CLASS_META_KEY, specification.getClass().getCanonicalName());
//            return specificationElement;
//        }
//    }
//
//    public static class ConstraintDeserializer implements JsonDeserializer<Specification> {
//        @Override
//        public Specification deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
//            return deseralizeAbstractClass(jsonElement, jsonDeserializationContext);
//        }
//
//    }
}
