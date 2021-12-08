package objects.misc;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time implements Comparable<Time> {
    final boolean pm;
    final int minutes;
    final int hour12;
    final int hour23;
    final int armyTime;

    public Time(String twelveHourTime) {
        Pattern twelveHourPattern = Pattern.compile("(\\d{1,2}):(\\d{2}) ?(AM|PM)");
        Matcher matcher = twelveHourPattern.matcher(twelveHourTime);
        if (!matcher.matches()) {
            throw new JsonParseException(String.format("Time '%s' does not match 12 hour format: 'HH:MM AM/PM'", twelveHourTime));
        } else {
            pm = matcher.group(3).equalsIgnoreCase("PM");
            minutes = Integer.parseInt(matcher.group(2));

            hour12 = Integer.parseInt(matcher.group(1));
            hour23 = (pm ? (hour12 % 12) + 11 : (hour12 % 12)-1);

            armyTime = hour23 * 100 + minutes;
        }
    }

    public int minutesSinceMidnight() {
        return hour23 * 60 + minutes;
    }

    @Override
    public String toString() {
        return String.format("%d:%02d %s", hour12, minutes, pm ? "PM" : "AM");
    }

    @Override
    public int compareTo(Time o) {
        return armyTime - o.armyTime;
    }

    public int minutesUntil(Time endTime) {
        return (endTime.hour23 - this.hour23) * 60 + endTime.minutes - this.minutes;
    }

    public boolean isBeforeOrEqual(Time other) {
        return this.compareTo(other) < 0;
    }

    public static class Serializer implements JsonSerializer<Time> {
        @Override
        public JsonElement serialize(Time time, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(time.toString());
        }
    }

    public static class Deserializer implements JsonDeserializer<Time> {
        @Override
        public Time deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Time(jsonElement.getAsString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return armyTime == time.armyTime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(armyTime);
    }
}
