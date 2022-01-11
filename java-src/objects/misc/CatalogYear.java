package objects.misc;

import com.google.gson.*;
import exceptions.JSONParseException;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CatalogYear {
    int startYear, endYear;

    public CatalogYear(String catalogYear) throws JsonParseException {
        Matcher catalogYearMatcher = Pattern.compile("(\\d{4})-(\\d{4})").matcher(catalogYear);
        try {
            if (!catalogYearMatcher.matches()) throw new JsonParseException(String.format("Catalog year '%s' does not follow format 'yyyy-yyyy'", catalogYear));
            startYear = Integer.parseInt(catalogYearMatcher.group(1));
            endYear = Integer.parseInt(catalogYearMatcher.group(2));
            assert startYear + 1 == endYear;
        } catch (AssertionError e) {
            throw new JsonParseException(String.format("Parsing catalog year: '%s', start year must be one less than the end year", catalogYear));
        } catch (Exception e) {
            throw new JsonParseException(String.format("Parsing catalog year: %s", e.getMessage()));
        }
    }

    public CatalogYear(TermYear.Term term, String year) throws JSONParseException {
        try {
            int yearInt = Integer.parseInt(year);
            switch (term) {
                case jterm, spring, summer -> {
                    startYear = yearInt - 1;
                    endYear = yearInt;
                }
                case fall, winter -> {
                    startYear = yearInt;
                    endYear = yearInt + 1;
                }
            }
        } catch (NumberFormatException e) {
            throw new JSONParseException(String.format("Improperly formatted year '%s'", year));
        }
    }

    public CatalogYear increment(int incrementer) {
        return new CatalogYear(String.format("%d-%d", startYear+incrementer, endYear+incrementer));
    }

    @Override
    public String toString() {
        return String.format("%d-%d", startYear, endYear);
    }

    public static class Serializer implements JsonSerializer<CatalogYear> {
        @Override
        public JsonElement serialize(CatalogYear catalogYear, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(catalogYear.toString());
        }
    }

    public static class Deserializer implements JsonDeserializer<CatalogYear> {

        @Override
        public CatalogYear deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new CatalogYear(jsonElement.getAsString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CatalogYear that = (CatalogYear) o;
        return startYear == that.startYear && endYear == that.endYear;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startYear, endYear);
    }
}
