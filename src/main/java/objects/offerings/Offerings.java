package objects.offerings;

import com.google.gson.*;
import exceptions.JSONParseException;
import exceptions.OfferingException;
import objects.Link;
import objects.Linkable;
import objects.misc.TermYear;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class Offerings implements Linkable {
    private LinkedHashMap<TermYear, TermOfferings> terms;

    public Offerings(LinkedHashMap<TermYear, TermOfferings> terms) {
        this.terms = terms;
    }

    public static class Serializer implements JsonSerializer<Offerings> {
        @Override
        public JsonElement serialize(Offerings offerings, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject object = new JsonObject();

            TermOfferings.Serializer termOfferingsSerializer = new TermOfferings.Serializer();
            for (TermYear termYear : offerings.terms.keySet()) {
                JsonElement terms = termOfferingsSerializer.serialize(offerings.terms.get(termYear), TermOfferings.class, jsonSerializationContext);
                object.add(termYear.toString(), terms);
            }
            return object;
        }
    }

    public static class Deserializer implements JsonDeserializer<Offerings> {
        @Override
        public Offerings deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject object = jsonElement.getAsJsonObject();

            TermOfferings.Deserializer termOfferingsDeserializer = new TermOfferings.Deserializer();
            LinkedHashMap<TermYear, TermOfferings> terms = new LinkedHashMap<>();
            try {
                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    if (entry.getKey().equals("$schema")) continue;
                    TermYear termYear = new TermYear(entry.getKey());
                    TermOfferings termOfferings = termOfferingsDeserializer.deserialize(entry.getValue(), TermOfferings.class, jsonDeserializationContext);
                    terms.put(termYear, termOfferings);
                }
            } catch (JSONParseException e) {
                throw new JsonParseException(e.getMessage());
            }

            return new Offerings(terms);
        }
    }

    private transient Link link;
    @Override
    public void link(Link l) throws OfferingException {
        link = l;
        for (Map.Entry<TermYear, TermOfferings> entry : terms.entrySet()) {
            TermOfferings termOfferings = entry.getValue();
            termOfferings.termYear = entry.getKey();
            termOfferings.link(l);
        }
    }

    public TermOfferings getOfferings(TermYear termYear) throws OfferingException {
        if (terms.containsKey(termYear)) {
            return terms.get(termYear);
        } else {
            throw new OfferingException(String.format("There are no offerings included for the term '%s'", termYear));
        }
    }
}
