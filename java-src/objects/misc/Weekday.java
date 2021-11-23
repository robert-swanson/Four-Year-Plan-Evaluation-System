package objects.misc;

import exceptions.JSONParseException;

public enum Weekday {
    sunday, monday, tuesday, wednesday, thursday, friday, saturday;

    public String toString() {
        return switch (this) {
            case sunday -> "Sunday";
            case monday -> "Monday";
            case tuesday -> "Tuesday";
            case wednesday -> "Wednesday";
            case thursday -> "Thursday";
            case friday -> "Friday";
            case saturday -> "Saturday";
        };
    }

    public static Weekday fromString(String weekday) throws JSONParseException {
        return switch (weekday.toLowerCase()) {
            case "sundays" -> Weekday.sunday;
            case "mondays" -> Weekday.monday;
            case "tuesdays" -> Weekday.tuesday;
            case "wednesdays" -> Weekday.wednesday;
            case "thursdays" -> Weekday.thursday;
            case "fridays" -> Weekday.friday;
            case "saturdays" -> Weekday.saturday;
            default -> throw new JSONParseException("Unknown weekday: " + weekday);
        };
    }
}
