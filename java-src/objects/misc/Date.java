package objects.misc;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.DateTimeException;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Date implements Comparable<Date> {
    int year, month, day;

    public Date(String date) { // yyyy-mm-dd
        Pattern datePattern = Pattern.compile("(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})");
        Matcher matcher = datePattern.matcher(date);
        if (!matcher.matches()) {
            throw new JsonParseException(String.format("Date '%s' does not match format: 'YYYY-MM-DD'", date));
        } else {
            year = Integer.parseInt(matcher.group(1));
            month = Integer.parseInt(matcher.group(2));
            day = Integer.parseInt(matcher.group(3));
            if (year < 1900 || year > 5000) {
                throw new DateTimeException(String.format("Year '%d' out of expected bounds", year));
            } else if (month < 1 || month > 12) {
                throw new DateTimeException(String.format("Month '%d' out of expected bounds", month));
            } else if (day < 1 || day > 31) {
                throw new DateTimeException(String.format("Day '%d' out of expected bounds", day));
            }
        }
    }

    public Date(int year, int month, int day) {
        if (year < 1900 || year > 5000) {
            throw new DateTimeException(String.format("Year '%d' out of expected bounds", year));
        } else if (month < 1 || month > 12) {
            throw new DateTimeException(String.format("Month '%d' out of expected bounds", month));
        } else if (day < 1 || day > 31) {
            throw new DateTimeException(String.format("Day '%d' out of expected bounds", day));
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    private Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month, day);
        return calendar;
    }
    public Date getFirstDayOfWeek() {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
    }

    public Date getFirstDayOfNextWeek() {
        Calendar calendar = getCalendar();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.add(Calendar.WEEK_OF_YEAR, 1);
        return new Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DATE));
    }

    public int weeksDifference(Date other) {
        return Math.abs(getCalendar().get(Calendar.WEEK_OF_YEAR) - other.getCalendar().get(Calendar.WEEK_OF_YEAR));
    }

    @Override
    public String toString() {
        return String.format("%d-%d-%d", year, month, day);
    }

    public static final int MAX_DAY = 31;
    public static final int MAX_MONTH = 12;

    @Override
    public int compareTo(Date other) {
        if (other == null) {
            return 1;
        }
        return (this.day + this.month*MAX_DAY + this.year*MAX_MONTH) - (other.day + other.month*MAX_DAY + other.year*MAX_MONTH);
    }

    public static boolean datesInOrder(Date first, Date second) {
        if (first == null) {
            return true;
        } else if (second == null) {
            return false;
        } else {
            return (first.day + first.month*MAX_DAY + first.year*MAX_MONTH) - (second.day + second.month*MAX_DAY + second.year*MAX_MONTH) <= 0;
        }
    }

    public static boolean datesInOrderNotEqual(Date first, Date second) {
        if (Objects.equals(first, second)) {
            return false;
        } else {
            return datesInOrder(first, second);
        }
    }

    public static class Serializer implements JsonSerializer<Date> {
        @Override
        public JsonElement serialize(Date date, Type type, JsonSerializationContext jsonSerializationContext) {
            return new JsonPrimitive(date.toString());
        }
    }

    public static class Deserializer implements JsonDeserializer<Date> {
        @Override
        public Date deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            return new Date(jsonElement.getAsString());
        }
    }
}
